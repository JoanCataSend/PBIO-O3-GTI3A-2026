-- Archivo: schema.sql
-- Descripción: esquema reproducible de la base de datos MariaDB del Sprint 0.
-- Copyright: 2026 Joan (uso académico PBIO - UPV)
-- Fecha: 2026-10-01
-- Autor: Joan
-- Aportación: tablas Dispositivo, TipoMedida y Medida con integridad referencial.

CREATE TABLE IF NOT EXISTS Dispositivo (
    dispositivoId INT UNSIGNED
        NOT NULL AUTO_INCREMENT,
    uuid VARCHAR(64) NOT NULL,
    nombre VARCHAR(100) NOT NULL,

    PRIMARY KEY (dispositivoId),
    UNIQUE KEY uq_dispositivo_uuid (uuid)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS TipoMedida (
    tipoMedidaId INT UNSIGNED NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    unidad VARCHAR(20) NOT NULL,

    PRIMARY KEY (tipoMedidaId),
    UNIQUE KEY uq_tipo_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Medida (
    medidaId BIGINT UNSIGNED
        NOT NULL AUTO_INCREMENT,

    dispositivoId INT UNSIGNED NOT NULL,
    tipoMedidaId INT UNSIGNED NOT NULL,

    valor INT NOT NULL,
    contador TINYINT UNSIGNED NOT NULL,
    rssi SMALLINT NOT NULL,

    fechaHora DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (medidaId),

    CONSTRAINT fk_medida_dispositivo
        FOREIGN KEY (dispositivoId)
        REFERENCES Dispositivo(dispositivoId)
        ON DELETE RESTRICT,

    CONSTRAINT fk_medida_tipo
        FOREIGN KEY (tipoMedidaId)
        REFERENCES TipoMedida(tipoMedidaId)
        ON DELETE RESTRICT,

    CONSTRAINT chk_contador
        CHECK (contador BETWEEN 0 AND 255),

    INDEX idx_dispositivo_fecha
        (dispositivoId, fechaHora),

    INDEX idx_tipo_fecha
        (tipoMedidaId, fechaHora)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
