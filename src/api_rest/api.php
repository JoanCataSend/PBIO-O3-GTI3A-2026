<?php

/*
 * Archivo: api.php
 * Descripción: adaptador HTTP/JSON de la API REST del Sprint 0.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: traducción entre peticiones HTTP y funciones de Logica.php.
 */

declare(strict_types=1);

header(
    'Content-Type: application/json; charset=utf-8'
);

/*
 * En Plesk, server/ se copia dentro de /biometria.
 * En el repositorio, server/ es hermano de web/.
 * Este fallback permite usar el mismo api.php en ambos contextos.
 */
$rutaLogica = __DIR__ . '/server/Logica.php';

if (!is_file($rutaLogica)) {
    $rutaLogica = dirname(__DIR__) . '/server/Logica.php';
}

require_once $rutaLogica;

/*
 * --------------------
 * Diseño lógico: codigo:N, datos:JSON --> responder() -->
 * Descripción: fija el código HTTP, serializa la respuesta y finaliza la petición.
 * --------------------
 */
function responder(
    int $codigo,
    mixed $datos
): never {
    http_response_code($codigo);

    echo json_encode(
        $datos,
        JSON_UNESCAPED_UNICODE
        | JSON_UNESCAPED_SLASHES
    );

    exit;
}

try {

    $metodo =
        $_SERVER['REQUEST_METHOD'] ?? 'GET';

    if ($metodo === 'POST') {

        $texto =
            file_get_contents('php://input');

        $datos =
            json_decode(
                $texto ?: '',
                true
            );

        if (!is_array($datos)) {
            throw new InvalidArgumentException(
                'El cuerpo debe ser JSON'
            );
        }

        responder(
            201,
            insertarMedida($datos)
        );
    }

    if ($metodo !== 'GET') {

        responder(
            405,
            ['error' => 'Método no permitido']
        );
    }

    $accion =
        $_GET['accion'] ?? 'medidas';

    if ($accion === 'health') {

        responder(
            200,
            probarConexion()
        );
    }

    if ($accion === 'dispositivos') {

        responder(
            200,
            listarDispositivos()
        );
    }

    if ($accion === 'tipos') {

        responder(
            200,
            listarTiposMedida()
        );
    }

    if ($accion === 'ultima') {

        if (
            !isset(
                $_GET['dispositivoId'],
                $_GET['tipoMedidaId']
            )
        ) {
            throw new InvalidArgumentException(
                'Faltan dispositivoId y tipoMedidaId'
            );
        }

        responder(
            200,
            buscarUltimaMedida(
                (int)$_GET['dispositivoId'],
                (int)$_GET['tipoMedidaId']
            )
        );
    }

    $filtros = [
        'dispositivoId'
            => $_GET['dispositivoId'] ?? '',
        'tipoMedidaId'
            => $_GET['tipoMedidaId'] ?? '',
        'desde'
            => $_GET['desde'] ?? '',
        'hasta'
            => $_GET['hasta'] ?? ''
    ];

    responder(
        200,
        listarMedidas($filtros)
    );

} catch (InvalidArgumentException $e) {

    responder(
        400,
        ['error' => $e->getMessage()]
    );

} catch (DomainException $e) {

    responder(
        404,
        ['error' => $e->getMessage()]
    );

} catch (Throwable $e) {

    responder(
        500,
        [
            'error'
                => 'Error interno del servidor'
        ]
    );
}
