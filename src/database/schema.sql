-- Archivo: schema.sql
-- Descripción: esquema relacional simplificado de MariaDB para PBIO Sprint 0.
-- Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
-- Fecha: 2026-10-13
-- Autor: Joan Catala Sendra
-- Aportación: simplificación de persistencia según revisión docente del Sprint 0.

CREATE TABLE IF NOT EXISTS Dispositivo (
    dispositivoId CHAR(16) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    PRIMARY KEY (dispositivoId),
    CONSTRAINT chk_dispositivo_id_longitud
        CHECK (CHAR_LENGTH(dispositivoId) = 16)
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
    dispositivoId CHAR(16) NOT NULL,
    tipoMedidaId TINYINT UNSIGNED NOT NULL,
    valor INT NOT NULL,
    fechaHora DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    PRIMARY KEY (dispositivoId, fechaHora),

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

    CONSTRAINT chk_medida_valor_16bits
        CHECK (valor BETWEEN -32768 AND 65535)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
