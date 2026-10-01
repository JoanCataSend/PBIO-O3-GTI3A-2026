<?php

declare(strict_types=1);

/*
 * LogicaUnitTest.php
 *
 * Tests automáticos de la lógica de negocio que no necesitan
 * conexión a la base de datos.
 *
 * Ejecutar desde la raíz del repositorio:
 *
 * php server/tests/LogicaUnitTest.php
 */

require_once __DIR__ . '/../Logica.php';

$testsEjecutados = 0;
$testsCorrectos = 0;

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

function esperarExcepcion(
    callable $funcion,
    string $claseEsperada,
    string $nombre
): void {
    try {
        $funcion();
    } catch (Throwable $e) {
        comprobar(
            $e instanceof $claseEsperada,
            $nombre
        );
        return;
    }

    comprobar(false, $nombre);
}


/*
 * 1. Una medida correcta debe superar la validación.
 */
$medidaValida = [
    'uuid' => 'EPSG-GTI-PROY-3A',
    'tipoMedidaId' => 14,
    'valor' => 123,
    'contador' => 16,
    'rssi' => -58
];

validarMedidaEntrada($medidaValida);
comprobar(
    true,
    'Una MedidaEntrada valida supera la validacion'
);


/*
 * 2. Deben estar presentes todos los campos obligatorios.
 */
$sinUuid = $medidaValida;
unset($sinUuid['uuid']);

esperarExcepcion(
    fn() => validarMedidaEntrada($sinUuid),
    InvalidArgumentException::class,
    'Se rechaza una medida sin uuid'
);


/*
 * 3. El uuid no puede estar vacío.
 */
$uuidVacio = $medidaValida;
$uuidVacio['uuid'] = '   ';

esperarExcepcion(
    fn() => validarMedidaEntrada($uuidVacio),
    InvalidArgumentException::class,
    'Se rechaza un uuid vacio'
);


/*
 * 4. Los campos numéricos deben ser enteros.
 */
$valorNoEntero = $medidaValida;
$valorNoEntero['valor'] = 'abc';

esperarExcepcion(
    fn() => validarMedidaEntrada($valorNoEntero),
    InvalidArgumentException::class,
    'Se rechaza un valor que no es entero'
);


/*
 * 5. El contador debe estar entre 0 y 255.
 */
$contadorNegativo = $medidaValida;
$contadorNegativo['contador'] = -1;

esperarExcepcion(
    fn() => validarMedidaEntrada($contadorNegativo),
    InvalidArgumentException::class,
    'Se rechaza contador menor que 0'
);

$contadorDemasiadoGrande = $medidaValida;
$contadorDemasiadoGrande['contador'] = 256;

esperarExcepcion(
    fn() => validarMedidaEntrada($contadorDemasiadoGrande),
    InvalidArgumentException::class,
    'Se rechaza contador mayor que 255'
);


/*
 * 6. Los límites 0 y 255 son válidos.
 */
$contadorCero = $medidaValida;
$contadorCero['contador'] = 0;
validarMedidaEntrada($contadorCero);

$contadorMaximo = $medidaValida;
$contadorMaximo['contador'] = 255;
validarMedidaEntrada($contadorMaximo);

comprobar(
    true,
    'Los contadores 0 y 255 son validos'
);


/*
 * 7. normalizarMedidaVista debe convertir los campos
 *    numéricos devueltos por MariaDB a enteros PHP.
 */
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
    'fechaHora' => '2026-10-01T10:00:00.000000'
];

$normalizada = normalizarMedidaVista($fila);

comprobar(
    is_int($normalizada['medidaId'])
    && is_int($normalizada['dispositivoId'])
    && is_int($normalizada['tipoMedidaId'])
    && is_int($normalizada['valor'])
    && is_int($normalizada['contador'])
    && is_int($normalizada['rssi']),
    'MedidaVista normaliza los campos numericos a enteros'
);

comprobar(
    $normalizada['valor'] === 123
    && $normalizada['rssi'] === -58,
    'MedidaVista conserva correctamente los valores numericos'
);


/*
 * 8. La consulta base debe unir Medida, Dispositivo y TipoMedida.
 */
$sql = consultaMedidaVista();

comprobar(
    str_contains($sql, 'FROM Medida m')
    && str_contains($sql, 'INNER JOIN Dispositivo d')
    && str_contains($sql, 'INNER JOIN TipoMedida t'),
    'La consulta MedidaVista utiliza las tres tablas del diseño'
);


echo PHP_EOL;
echo "Resultado: $testsCorrectos/$testsEjecutados tests correctos." . PHP_EOL;
echo "LOGICA UNIT TEST: OK" . PHP_EOL;
