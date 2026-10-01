-- Archivo: drop.sql
-- Descripción: elimina las tablas del proyecto en orden seguro por dependencias.
-- Copyright: 2026 Joan (uso académico PBIO - UPV)
-- Fecha: 2026-10-01
-- Autor: Joan
-- Aportación: script reproducible para reinicializar el esquema.

DROP TABLE IF EXISTS Medida;
DROP TABLE IF EXISTS TipoMedida;
DROP TABLE IF EXISTS Dispositivo;
