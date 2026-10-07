<?php

/*
 * Archivo: ApiIntegracionTest.php
 * Descripción: prueba automática de integración HTTPS contra la API desplegada.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: comprobación de la cadena HTTPS -> REST -> lógica -> MariaDB.
 */

declare(strict_types=1);

$apiUrl = getenv('PBIO_API_URL') ?: 'https://jcatsen.upv.edu.es/biometria/api.php';
$testsEjecutados = 0;
$testsCorrectos = 0;

/*
 * --------------------
 * Diseño lógico: condicion: B, nombre: Text --> comprobar() -->
 * Descripción: registra un criterio cumplido o termina al primer fallo.
 * --------------------
 */
function comprobar(bool $condicion, string $nombre): void
{
    global $testsEjecutados, $testsCorrectos;

    $testsEjecutados++;

    if (!$condicion) {
        echo "[FALLO] $nombre" . PHP_EOL;
        exit(1);
    }

    $testsCorrectos++;
    echo "[OK] $nombre" . PHP_EOL;
}

/*
 * --------------------
 * Diseño lógico: url: Text --> getJson() --> datos: Json
 * Descripción: realiza una petición GET HTTPS y decodifica un objeto/colección JSON.
 * --------------------
 */
function getJson(string $url): array
{
    $contexto = stream_context_create([
        'http' => [
            'method' => 'GET',
            'timeout' => 10,
            'ignore_errors' => true
        ]
    ]);

    $respuesta = @file_get_contents($url, false, $contexto);

    if ($respuesta === false) {
        throw new RuntimeException('No se pudo acceder a ' . $url);
    }

    $datos = json_decode($respuesta, true, 512, JSON_THROW_ON_ERROR);

    if (!is_array($datos)) {
        throw new RuntimeException('La respuesta no contiene JSON válido');
    }

    return $datos;
}

$health = getJson($apiUrl . '?accion=health');
comprobar(($health['ok'] ?? false) === true, 'Health responde ok=true');
comprobar(
    ($health['database'] ?? '') === 'jcatsen_pbio',
    'Health confirma la base jcatsen_pbio'
);

$dispositivos = getJson($apiUrl . '?accion=dispositivos');
$dispositivoEncontrado = false;
foreach ($dispositivos as $dispositivo) {
    if (($dispositivo['uuid'] ?? '') === 'EPSG-GTI-PROY-3A'
        && ($dispositivo['nombre'] ?? '') === 'GTI Joan') {
        $dispositivoEncontrado = true;
        break;
    }
}
comprobar($dispositivoEncontrado, 'La API devuelve el dispositivo GTI Joan');

$tipos = getJson($apiUrl . '?accion=tipos');
$o3Encontrado = false;
$temperaturaEncontrada = false;
foreach ($tipos as $tipo) {
    if ((int)($tipo['tipoMedidaId'] ?? -1) === 14
        && ($tipo['nombre'] ?? '') === 'O3'
        && ($tipo['unidad'] ?? '') === 'ppb') {
        $o3Encontrado = true;
    }

    if ((int)($tipo['tipoMedidaId'] ?? -1) === 12
        && ($tipo['nombre'] ?? '') === 'Temperatura'
        && ($tipo['unidad'] ?? '') === '°C') {
        $temperaturaEncontrada = true;
    }
}
comprobar($o3Encontrado, 'La API devuelve O3 con ID 14 y unidad ppb');
comprobar($temperaturaEncontrada, 'La API devuelve Temperatura con ID 12 y unidad °C');

$medidas = getJson($apiUrl . '?accion=medidas');
comprobar(is_array($medidas), 'La API devuelve una colección de medidas');

echo PHP_EOL;
echo "Resultado: $testsCorrectos/$testsEjecutados tests correctos." . PHP_EOL;
echo "API INTEGRACION TEST: OK" . PHP_EOL;
