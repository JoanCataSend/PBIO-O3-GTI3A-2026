<?php

/*
 * Archivo: LogicaUnitTest.php
 * Descripción: pruebas unitarias automáticas de la lógica sin conexión a MariaDB.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: criterios reproducibles de validación, normalización y consulta.
 */

declare(strict_types=1);

require_once __DIR__ . '/../Logica.php';

$testsEjecutados = 0;
$testsCorrectos = 0;

/*
 * --------------------
 * Diseño lógico: condicion: B, nombre: Text --> comprobar() -->
 * Descripción: termina el proceso si una condición de prueba no se cumple.
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
 * Diseño lógico: clase_esperada: Text, nombre: Text --> esperarExcepcion() -->
 * Descripción: verifica que una operación rechazada lance la excepción esperada.
 * --------------------
 */
function esperarExcepcion(
    callable $funcion,
    string $claseEsperada,
    string $nombre
): void {
    try {
        $funcion();
    } catch (Throwable $e) {
        comprobar($e instanceof $claseEsperada, $nombre);
        return;
    }

    comprobar(false, $nombre);
}

$medidaValida = [
    'uuid' => 'EPSG-GTI-PROY-3A',
    'tipoMedidaId' => 14,
    'valor' => 123,
    'contador' => 16,
    'rssi' => -58
];

validarMedidaEntrada($medidaValida);
comprobar(true, 'Una MedidaEntrada válida supera la validación');

$sinUuid = $medidaValida;
unset($sinUuid['uuid']);
esperarExcepcion(
    fn() => validarMedidaEntrada($sinUuid),
    InvalidArgumentException::class,
    'Se rechaza una medida sin uuid'
);

$uuidVacio = $medidaValida;
$uuidVacio['uuid'] = '';
esperarExcepcion(
    fn() => validarMedidaEntrada($uuidVacio),
    InvalidArgumentException::class,
    'Se rechaza un uuid vacío'
);

$uuidCorto = $medidaValida;
$uuidCorto['uuid'] = 'ABC';
esperarExcepcion(
    fn() => validarMedidaEntrada($uuidCorto),
    InvalidArgumentException::class,
    'Se rechaza un uuid que no ocupa 16 caracteres'
);

$valorNoEntero = $medidaValida;
$valorNoEntero['valor'] = 'abc';
esperarExcepcion(
    fn() => validarMedidaEntrada($valorNoEntero),
    InvalidArgumentException::class,
    'Se rechaza un valor no entero'
);

$contadorNegativo = $medidaValida;
$contadorNegativo['contador'] = -1;
esperarExcepcion(
    fn() => validarMedidaEntrada($contadorNegativo),
    InvalidArgumentException::class,
    'Se rechaza contador menor que 0'
);

$contadorGrande = $medidaValida;
$contadorGrande['contador'] = 256;
esperarExcepcion(
    fn() => validarMedidaEntrada($contadorGrande),
    InvalidArgumentException::class,
    'Se rechaza contador mayor que 255'
);

$valorPequeno = $medidaValida;
$valorPequeno['valor'] = -32769;
esperarExcepcion(
    fn() => validarMedidaEntrada($valorPequeno),
    InvalidArgumentException::class,
    'Se rechaza valor menor que el rango de Minor'
);

$valorGrande = $medidaValida;
$valorGrande['valor'] = 65536;
esperarExcepcion(
    fn() => validarMedidaEntrada($valorGrande),
    InvalidArgumentException::class,
    'Se rechaza valor mayor que el rango de Minor'
);

$contadorCero = $medidaValida;
$contadorCero['contador'] = 0;
validarMedidaEntrada($contadorCero);
$contadorMaximo = $medidaValida;
$contadorMaximo['contador'] = 255;
validarMedidaEntrada($contadorMaximo);
comprobar(true, 'Los contadores 0 y 255 son válidos');

$fila = [
    'medidaId' => '10',
    'dispositivoId' => '1',
    'uuid' => 'EPSG-GTI-PROY-3A',
    'dispositivo' => 'GTI Joan',
    'tipoMedidaId' => '14',
    'tipoMedida' => 'O3',
    'unidad' => 'ppb',
    'valor' => '123',
    'contador' => '16',
    'rssi' => '-58',
    'fechaHora' => '2026-10-07T10:00:00'
];

$normalizada = normalizarMedidaVista($fila);
comprobar(
    is_int($normalizada['medidaId'])
    && is_int($normalizada['dispositivoId'])
    && is_int($normalizada['tipoMedidaId'])
    && is_int($normalizada['valor'])
    && is_int($normalizada['contador'])
    && is_int($normalizada['rssi']),
    'MedidaVista normaliza sus campos numéricos'
);

comprobar(
    $normalizada['valor'] === 123 && $normalizada['rssi'] === -58,
    'MedidaVista conserva los valores después de normalizar'
);

$sql = consultaMedidaVista();
comprobar(
    str_contains($sql, 'FROM Medida m')
    && str_contains($sql, 'INNER JOIN Dispositivo d')
    && str_contains($sql, 'INNER JOIN TipoMedida t'),
    'La vista de medida une exactamente las tres tablas del diseño'
);

echo PHP_EOL;
echo "Resultado: $testsCorrectos/$testsEjecutados tests correctos." . PHP_EOL;
echo "LOGICA UNIT TEST: OK" . PHP_EOL;
