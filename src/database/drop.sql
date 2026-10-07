-- Archivo: drop.sql
-- Descripción: elimina el esquema PBIO en orden seguro para poder recrearlo.
-- Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
-- Fecha: 2026-10-07
-- Autor: Joan Catala Sendra
-- Aportación: reinicio reproducible del esquema de Sprint 0.

DROP TABLE IF EXISTS Medida;
DROP TABLE IF EXISTS TipoMedida;
DROP TABLE IF EXISTS Dispositivo;
