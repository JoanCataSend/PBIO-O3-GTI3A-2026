-- Archivo: seed.sql
-- Descripción: datos iniciales reproducibles del dispositivo y tipos de medida.
-- Copyright: 2026 Joan (uso académico PBIO - UPV)
-- Fecha: 2026-10-01
-- Autor: Joan
-- Aportación: datos iniciales reproducibles del Sprint 0.

INSERT INTO Dispositivo (uuid, nombre)
VALUES ('EPSG-GTI-PROY-3A', 'GTI Joan')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre);

INSERT INTO TipoMedida
    (tipoMedidaId, nombre, unidad)
VALUES
    (11, 'CO2', 'ppm'),
    (12, 'Temperatura', '°C'),
    (13, 'Ruido', 'dB'),
    (14, 'O3', 'ppb')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    unidad = VALUES(unidad);
