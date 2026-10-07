-- Archivo: seed.sql
-- Descripción: datos de catálogo estrictamente necesarios para el Sprint 0.
-- Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
-- Fecha: 2026-10-07
-- Autor: Joan Catala Sendra
-- Aportación: dispositivo del proyecto y los dos tipos realmente publicados.

INSERT INTO Dispositivo (uuid, nombre)
VALUES ('EPSG-GTI-PROY-3A', 'GTI Joan')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre);

INSERT INTO TipoMedida (tipoMedidaId, nombre, unidad)
VALUES
    (12, 'Temperatura', '°C'),
    (14, 'O3', 'ppb')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    unidad = VALUES(unidad);
