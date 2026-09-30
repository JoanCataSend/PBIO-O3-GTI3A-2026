<?php

declare(strict_types=1);

function conexionBD(): mysqli
{
    static $conexion = null;

    if ($conexion instanceof mysqli) {
        return $conexion;
    }

    $config = require __DIR__ . '/SDBaseDatos.php';

    $conexion = new mysqli(
        $config['host'],
        $config['user'],
        $config['password'],
        $config['database'],
        (int)$config['port']
    );

    if ($conexion->connect_errno) {
        throw new RuntimeException(
            'No se pudo conectar con MariaDB'
        );
    }

    $conexion->set_charset('utf8mb4');

    return $conexion;
}

function probarConexion(): array
{
    $bd = conexionBD();

    $resultado = $bd->query('SELECT 1 AS ok');

    if (!$resultado) {
        throw new RuntimeException(
            'La consulta de prueba ha fallado'
        );
    }

    return [
        'ok' => true,
        'database' => 'jcatsen_pbio'
    ];
}

// datos: MedidaEntrada --> insertarMedida() --> MedidaVista
function insertarMedida(array $datos): array
{
    validarMedidaEntrada($datos);

    $bd = conexionBD();

    $uuid = (string)$datos['uuid'];
    $tipoMedidaId = (int)$datos['tipoMedidaId'];
    $valor = (int)$datos['valor'];
    $contador = (int)$datos['contador'];
    $rssi = (int)$datos['rssi'];

    $stmt = $bd->prepare(
        'SELECT dispositivoId
         FROM Dispositivo
         WHERE uuid = ?'
    );

    $stmt->bind_param('s', $uuid);
    $stmt->execute();

    $filaDispositivo =
        $stmt->get_result()->fetch_assoc();

    if (!$filaDispositivo) {
        throw new DomainException(
            'No existe un dispositivo con ese uuid'
        );
    }

    $dispositivoId =
        (int)$filaDispositivo['dispositivoId'];

    $stmt = $bd->prepare(
        'SELECT tipoMedidaId
         FROM TipoMedida
         WHERE tipoMedidaId = ?'
    );

    $stmt->bind_param(
        'i',
        $tipoMedidaId
    );

    $stmt->execute();

    if (!$stmt->get_result()->fetch_assoc()) {
        throw new DomainException(
            'No existe ese tipoMedidaId'
        );
    }

    $stmt = $bd->prepare(
        'INSERT INTO Medida
            (
                dispositivoId,
                tipoMedidaId,
                valor,
                contador,
                rssi
            )
         VALUES (?, ?, ?, ?, ?)'
    );

    $stmt->bind_param(
        'iiiii',
        $dispositivoId,
        $tipoMedidaId,
        $valor,
        $contador,
        $rssi
    );

    $stmt->execute();

    return buscarMedidaConId(
        (int)$bd->insert_id
    );
}

// medidaId: N --> buscarMedidaConId() --> MedidaVista
function buscarMedidaConId(int $medidaId): array
{
    $bd = conexionBD();

    $stmt = $bd->prepare(
        consultaMedidaVista()
        . ' WHERE m.medidaId = ?'
    );

    $stmt->bind_param(
        'i',
        $medidaId
    );

    $stmt->execute();

    $fila =
        $stmt->get_result()->fetch_assoc();

    if (!$fila) {
        throw new DomainException(
            'No existe esa medida'
        );
    }

    return normalizarMedidaVista($fila);
}

// filtros --> listarMedidas() --> [MedidaVista]
function listarMedidas(array $filtros = []): array
{
    $bd = conexionBD();

    $where = [];
    $tipos = '';
    $valores = [];

    if (
        isset($filtros['dispositivoId'])
        && $filtros['dispositivoId'] !== ''
    ) {
        $where[] = 'm.dispositivoId = ?';
        $tipos .= 'i';
        $valores[] =
            (int)$filtros['dispositivoId'];
    }

    if (
        isset($filtros['tipoMedidaId'])
        && $filtros['tipoMedidaId'] !== ''
    ) {
        $where[] = 'm.tipoMedidaId = ?';
        $tipos .= 'i';
        $valores[] =
            (int)$filtros['tipoMedidaId'];
    }

    if (!empty($filtros['desde'])) {
        $where[] = 'm.fechaHora >= ?';
        $tipos .= 's';
        $valores[] =
            (string)$filtros['desde'];
    }

    if (!empty($filtros['hasta'])) {
        $where[] = 'm.fechaHora <= ?';
        $tipos .= 's';
        $valores[] =
            (string)$filtros['hasta'];
    }

    $sql = consultaMedidaVista();

    if ($where) {
        $sql .= ' WHERE '
            . implode(' AND ', $where);
    }

    $sql .= ' ORDER BY m.fechaHora DESC, m.medidaId DESC LIMIT 500';

    $stmt = $bd->prepare($sql);

    if ($tipos !== '') {
        $stmt->bind_param(
            $tipos,
            ...$valores
        );
    }

    $stmt->execute();

    $resultado =
        $stmt->get_result();

    $medidas = [];

    while ($fila = $resultado->fetch_assoc()) {
        $medidas[] =
            normalizarMedidaVista($fila);
    }

    return $medidas;
}

// listarDispositivos() --> [Dispositivo]
function listarDispositivos(): array
{
    $bd = conexionBD();

    $resultado = $bd->query(
        'SELECT dispositivoId, uuid, nombre
         FROM Dispositivo
         ORDER BY nombre'
    );

    return $resultado->fetch_all(
        MYSQLI_ASSOC
    );
}

// listarTiposMedida() --> [TipoMedida]
function listarTiposMedida(): array
{
    $bd = conexionBD();

    $resultado = $bd->query(
        'SELECT tipoMedidaId, nombre, unidad
         FROM TipoMedida
         ORDER BY tipoMedidaId'
    );

    return $resultado->fetch_all(
        MYSQLI_ASSOC
    );
}

// dispositivoId:N, tipoMedidaId:N --> buscarUltimaMedida() --> MedidaVista
function buscarUltimaMedida(
    int $dispositivoId,
    int $tipoMedidaId
): array {
    $bd = conexionBD();

    $sql =
        consultaMedidaVista()
        . ' WHERE m.dispositivoId = ?
              AND m.tipoMedidaId = ?
            ORDER BY m.fechaHora DESC,
                     m.medidaId DESC
            LIMIT 1';

    $stmt = $bd->prepare($sql);

    $stmt->bind_param(
        'ii',
        $dispositivoId,
        $tipoMedidaId
    );

    $stmt->execute();

    $fila =
        $stmt->get_result()->fetch_assoc();

    if (!$fila) {
        throw new DomainException(
            'No hay medidas para esa combinación'
        );
    }

    return normalizarMedidaVista($fila);
}

function validarMedidaEntrada(
    array $datos
): void {
    $campos = [
        'uuid',
        'tipoMedidaId',
        'valor',
        'contador',
        'rssi'
    ];

    foreach ($campos as $campo) {
        if (!array_key_exists($campo, $datos)) {
            throw new InvalidArgumentException(
                'Falta el campo ' . $campo
            );
        }
    }

    if (
        !is_string($datos['uuid'])
        || trim($datos['uuid']) === ''
    ) {
        throw new InvalidArgumentException(
            'uuid no válido'
        );
    }

    foreach (
        ['tipoMedidaId', 'valor', 'contador', 'rssi']
        as $campo
    ) {
        if (
            filter_var(
                $datos[$campo],
                FILTER_VALIDATE_INT
            ) === false
        ) {
            throw new InvalidArgumentException(
                $campo . ' debe ser entero'
            );
        }
    }

    $contador =
        (int)$datos['contador'];

    if (
        $contador < 0
        || $contador > 255
    ) {
        throw new InvalidArgumentException(
            'contador fuera de rango'
        );
    }
}

function consultaMedidaVista(): string
{
    return
        'SELECT
            m.medidaId,
            d.dispositivoId,
            d.uuid,
            d.nombre AS dispositivo,
            t.tipoMedidaId,
            t.nombre AS tipoMedida,
            t.unidad,
            m.valor,
            m.contador,
            m.rssi,
            DATE_FORMAT(
                m.fechaHora,
                "%Y-%m-%dT%H:%i:%s.%f"
            ) AS fechaHora
         FROM Medida m
         INNER JOIN Dispositivo d
            ON d.dispositivoId = m.dispositivoId
         INNER JOIN TipoMedida t
            ON t.tipoMedidaId = m.tipoMedidaId';
}

function normalizarMedidaVista(
    array $fila
): array {
    foreach (
        [
            'medidaId',
            'dispositivoId',
            'tipoMedidaId',
            'valor',
            'contador',
            'rssi'
        ]
        as $campo
    ) {
        if (isset($fila[$campo])) {
            $fila[$campo] =
                (int)$fila[$campo];
        }
    }

    return $fila;
}
