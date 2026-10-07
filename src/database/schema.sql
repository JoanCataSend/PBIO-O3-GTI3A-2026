-- Archivo: schema.sql
-- Descripción: esquema relacional reproducible de MariaDB para PBIO Sprint 0.
-- Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
-- Fecha: 2026-10-07
-- Autor: Joan Catala Sendra
-- Aportación: modelo normalizado mínimo de dispositivos, tipos y medidas.

CREATE TABLE IF NOT EXISTS Dispositivo (
    dispositivoId INT UNSIGNED NOT NULL AUTO_INCREMENT,
    uuid CHAR(16) NOT NULL,
    nombre VARCHAR(100) NOT NULL,

    PRIMARY KEY (dispositivoId),
    UNIQUE KEY uq_dispositivo_uuid (uuid),
    CONSTRAINT chk_dispositivo_uuid_longitud
        CHECK (CHAR_LENGTH(uuid) = 16)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS TipoMedida (
    tipoMedidaId TINYINT UNSIGNED NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    unidad VARCHAR(20) NOT NULL,

    PRIMARY KEY (tipoMedidaId),
    UNIQUE KEY uq_tipo_nombre (nombre)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Medida (
    medidaId BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    dispositivoId INT UNSIGNED NOT NULL,
    tipoMedidaId TINYINT UNSIGNED NOT NULL,
    valor INT NOT NULL,
    contador TINYINT UNSIGNED NOT NULL,
    rssi SMALLINT NOT NULL,
    fechaHora DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (medidaId),

    CONSTRAINT fk_medida_dispositivo
        FOREIGN KEY (dispositivoId)
        REFERENCES Dispositivo(dispositivoId)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_medida_tipo
        FOREIGN KEY (tipoMedidaId)
        REFERENCES TipoMedida(tipoMedidaId)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_medida_contador
        CHECK (contador BETWEEN 0 AND 255),

    CONSTRAINT chk_medida_valor_16bits
        CHECK (valor BETWEEN -32768 AND 65535),

    INDEX idx_medida_dispositivo_tipo_fecha
        (dispositivoId, tipoMedidaId, fechaHora, medidaId),

    INDEX idx_medida_tipo_fecha
        (tipoMedidaId, fechaHora, medidaId)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
