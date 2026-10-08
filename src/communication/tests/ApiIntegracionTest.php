<?php

/*
 * Archivo: ApiIntegracionTest.php
 * Descripción: pruebas automáticas reproducibles del contrato REST y, de forma
 *              opcional, pruebas de integración contra una API desplegada.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: validación offline del adaptador y validación HTTP real opt-in.
 */

declare(strict_types=1);

$apiUrl = trim((string)(getenv('PBIO_API_URL') ?: ''));
$permitirEscritura = getenv('PBIO_API_WRITE_TEST') === '1';
$testsEjecutados = 0;
$testsCorrectos = 0;

/*
 * --------------------
 * Diseño lógico: condicion: B, nombre: Text --> comprobar()
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
 * Diseño lógico: texto: Text, fragmento: Text --> contiene() --> encontrado: B
 * Descripción: comprueba de forma literal que el adaptador contiene un elemento del contrato.
 * --------------------
 */
function contiene(string $texto, string $fragmento): bool
{
    return strpos($texto, $fragmento) !== false;
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

/*
 * --------------------
 * Diseño lógico: ejecutarContratoOffline()
 * Descripción: valida sin red el despacho, la dependencia permitida y la ausencia de SQL.
 * --------------------
 */
function ejecutarContratoOffline(): void
{
    $rutaApi = dirname(__DIR__) . '/api.php';
    $codigo = file_get_contents($rutaApi);

    comprobar($codigo !== false, 'api.php se puede leer');
    comprobar(contiene($codigo, 'require_once $rutaLogica'), 'communication carga business_logic');
    comprobar(contiene($codigo, "case 'health':"), 'Existe acción health');
    comprobar(contiene($codigo, "case 'dispositivos':"), 'Existe acción dispositivos');
    comprobar(contiene($codigo, "case 'tipos':"), 'Existe acción tipos');
    comprobar(contiene($codigo, "case 'ultima':"), 'Existe acción ultima');
    comprobar(contiene($codigo, "case 'medidas':"), 'Existe acción medidas');
    comprobar(contiene($codigo, 'insertarMedida($datos)'), 'POST delega en insertarMedida');
    comprobar(contiene($codigo, 'responder(405'), 'Método no soportado se traduce a 405');
    comprobar(contiene($codigo, 'catch (InvalidArgumentException'), 'Entrada inválida se traduce desde InvalidArgumentException');
    comprobar(contiene($codigo, 'responder(400'), 'Entrada inválida se traduce a 400');
    comprobar(contiene($codigo, 'catch (DomainException'), 'Entidad ausente se traduce desde DomainException');
    comprobar(contiene($codigo, 'responder(404'), 'Entidad ausente se traduce a 404');
    comprobar(contiene($codigo, "responder(500, ['error' => 'Error interno del servidor'])"), 'Errores no previstos se traducen a 500 genérico');

    $patronesSql = [
        '/\\bSELECT\\b/i', '/\\bINSERT\\s+INTO\\b/i', '/\\bUPDATE\\b/i',
        '/\\bDELETE\\s+FROM\\b/i', '/\\bCREATE\\s+TABLE\\b/i'
    ];

    foreach ($patronesSql as $patron) {
        comprobar(preg_match($patron, $codigo) !== 1, 'communication no contiene SQL: ' . $patron);
    }

    echo "[INFO] Contrato offline completado. Defina PBIO_API_URL para añadir pruebas HTTP reales." . PHP_EOL;
}

/*
 * --------------------
 * Diseño lógico: url: Text, permitir_escritura: B --> ejecutarIntegracionLive()
 * Descripción: valida por HTTP una API desplegada sin escribir salvo autorización explícita.
 * --------------------
 */
function ejecutarIntegracionLive(string $url, bool $permitirEscritura): void
{
    $health = peticionJson($url . '?accion=health');
    comprobar($health['codigo'] === 200, 'Health responde HTTP 200');
    comprobar(($health['cuerpo']['ok'] ?? false) === true, 'Health responde ok=true');

    $respuestaDispositivos = peticionJson($url . '?accion=dispositivos');
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

    $respuestaTipos = peticionJson($url . '?accion=tipos');
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

    $respuestaMedidas = peticionJson($url . '?accion=medidas');
    comprobar($respuestaMedidas['codigo'] === 200, 'Medidas responde HTTP 200');
    comprobar(is_array($respuestaMedidas['cuerpo']), 'La API devuelve una colección de medidas');

    $accionDesconocida = peticionJson($url . '?accion=no_existe');
    comprobar($accionDesconocida['codigo'] === 400, 'Una acción desconocida devuelve HTTP 400');

    $ultimaSinParametros = peticionJson($url . '?accion=ultima');
    comprobar($ultimaSinParametros['codigo'] === 400, 'Ultima sin parámetros devuelve HTTP 400');

    $ultimaInexistente = peticionJson(
        $url . '?accion=ultima&dispositivoId=2147483647&tipoMedidaId=14'
    );
    comprobar($ultimaInexistente['codigo'] === 404, 'Una medida inexistente devuelve HTTP 404');

    $metodoNoPermitido = peticionJson($url, 'PUT');
    comprobar($metodoNoPermitido['codigo'] === 405, 'PUT devuelve HTTP 405');

    $postInvalido = peticionJson($url, 'POST', '{"uuid":');
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

        $postValido = peticionJson($url, 'POST', $cuerpo);
        comprobar($postValido['codigo'] === 201, 'POST válido devuelve HTTP 201');
        comprobar(
            (int)($postValido['cuerpo']['valor'] ?? -1) === 321
            && (int)($postValido['cuerpo']['tipoMedidaId'] ?? -1) === 14,
            'POST válido persiste y devuelve la medida O3 esperada'
        );
    } else {
        echo "[INFO] POST válido omitido. Use PBIO_API_WRITE_TEST=1 para habilitar escritura real." . PHP_EOL;
    }
}

ejecutarContratoOffline();

if ($apiUrl !== '') {
    ejecutarIntegracionLive($apiUrl, $permitirEscritura);
} else {
    echo "[INFO] Integración HTTP real omitida: PBIO_API_URL no está definida." . PHP_EOL;
}

echo PHP_EOL;
echo "Resultado: $testsCorrectos/$testsEjecutados tests correctos." . PHP_EOL;
echo "API CONTRATO/INTEGRACION TEST: OK" . PHP_EOL;
