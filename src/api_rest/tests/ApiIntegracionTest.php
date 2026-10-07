<?php

/*
 * Archivo: ApiIntegracionTest.php
 * Descripción: pruebas automáticas de integración HTTPS contra la API desplegada.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: comprobación de lectura, despacho HTTP, errores y POST opcional.
 */

declare(strict_types=1);

$apiUrl = getenv('PBIO_API_URL') ?: 'https://jcatsen.upv.edu.es/biometria/api.php';
$permitirEscritura = getenv('PBIO_API_WRITE_TEST') === '1';
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
 * Diseño lógico: url: Text, metodo: Text, cuerpo: Text --> peticionJson() --> respuesta: RespuestaHTTP
 * Descripción: ejecuta una petición HTTP/HTTPS, conserva su código y decodifica el JSON.
 * --------------------
 */
function peticionJson(
    string $url,
    string $metodo = 'GET',
    ?string $cuerpo = null
): array {
    $cabeceras = ['Accept: application/json'];

    if ($cuerpo !== null) {
        $cabeceras[] = 'Content-Type: application/json; charset=utf-8';
    }

    $opciones = [
        'method' => $metodo,
        'timeout' => 10,
        'ignore_errors' => true,
        'header' => implode("\r\n", $cabeceras)
    ];

    if ($cuerpo !== null) {
        $opciones['content'] = $cuerpo;
    }

    $contexto = stream_context_create(['http' => $opciones]);
    $respuesta = @file_get_contents($url, false, $contexto);

    if ($respuesta === false) {
        throw new RuntimeException('No se pudo acceder a ' . $url);
    }

    $lineaEstado = $http_response_header[0] ?? '';

    if (!preg_match('/\s(\d{3})\s/', $lineaEstado, $coincidencia)) {
        throw new RuntimeException('No se pudo determinar el código HTTP');
    }

    $datos = json_decode($respuesta, true, 512, JSON_THROW_ON_ERROR);

    if (!is_array($datos)) {
        throw new RuntimeException('La respuesta no contiene JSON válido');
    }

    return [
        'codigo' => (int)$coincidencia[1],
        'cuerpo' => $datos
    ];
}

$health = peticionJson($apiUrl . '?accion=health');
comprobar($health['codigo'] === 200, 'Health responde HTTP 200');
comprobar(($health['cuerpo']['ok'] ?? false) === true, 'Health responde ok=true');
comprobar(
    ($health['cuerpo']['database'] ?? '') === 'jcatsen_pbio',
    'Health confirma la base jcatsen_pbio'
);

$respuestaDispositivos = peticionJson($apiUrl . '?accion=dispositivos');
comprobar($respuestaDispositivos['codigo'] === 200, 'Dispositivos responde HTTP 200');
$dispositivos = $respuestaDispositivos['cuerpo'];
$dispositivoEncontrado = false;
foreach ($dispositivos as $dispositivo) {
    if (($dispositivo['uuid'] ?? '') === 'EPSG-GTI-PROY-3A'
        && ($dispositivo['nombre'] ?? '') === 'GTI Joan') {
        $dispositivoEncontrado = true;
        break;
    }
}
comprobar($dispositivoEncontrado, 'La API devuelve el dispositivo GTI Joan');

$respuestaTipos = peticionJson($apiUrl . '?accion=tipos');
comprobar($respuestaTipos['codigo'] === 200, 'Tipos responde HTTP 200');
$tipos = $respuestaTipos['cuerpo'];
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

$respuestaMedidas = peticionJson($apiUrl . '?accion=medidas');
comprobar($respuestaMedidas['codigo'] === 200, 'Medidas responde HTTP 200');
comprobar(is_array($respuestaMedidas['cuerpo']), 'La API devuelve una colección de medidas');

$accionDesconocida = peticionJson($apiUrl . '?accion=no_existe');
comprobar($accionDesconocida['codigo'] === 400, 'Una acción desconocida devuelve HTTP 400');

$ultimaSinParametros = peticionJson($apiUrl . '?accion=ultima');
comprobar($ultimaSinParametros['codigo'] === 400, 'Ultima sin parámetros devuelve HTTP 400');

$ultimaInexistente = peticionJson(
    $apiUrl . '?accion=ultima&dispositivoId=2147483647&tipoMedidaId=14'
);
comprobar($ultimaInexistente['codigo'] === 404, 'Una medida inexistente devuelve HTTP 404');

$metodoNoPermitido = peticionJson($apiUrl, 'PUT');
comprobar($metodoNoPermitido['codigo'] === 405, 'PUT devuelve HTTP 405');

$postInvalido = peticionJson($apiUrl, 'POST', '{"uuid":');
comprobar($postInvalido['codigo'] === 400, 'POST con JSON inválido devuelve HTTP 400');

if ($permitirEscritura) {
    $cuerpo = json_encode(
        [
            'uuid' => 'EPSG-GTI-PROY-3A',
            'tipoMedidaId' => 14,
            'valor' => 321,
            'contador' => 250,
            'rssi' => -50
        ],
        JSON_THROW_ON_ERROR
    );

    $postValido = peticionJson($apiUrl, 'POST', $cuerpo);
    comprobar($postValido['codigo'] === 201, 'POST válido devuelve HTTP 201');
    comprobar(
        (int)($postValido['cuerpo']['valor'] ?? -1) === 321
        && (int)($postValido['cuerpo']['tipoMedidaId'] ?? -1) === 14,
        'POST válido persiste y devuelve la medida O3 esperada'
    );
} else {
    echo "[INFO] POST válido omitido. Use PBIO_API_WRITE_TEST=1 para habilitar escritura real." . PHP_EOL;
}

echo PHP_EOL;
echo "Resultado: $testsCorrectos/$testsEjecutados tests correctos." . PHP_EOL;
echo "API INTEGRACION TEST: OK" . PHP_EOL;
