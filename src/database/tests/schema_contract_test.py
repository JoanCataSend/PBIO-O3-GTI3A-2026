"""
Archivo: schema_contract_test.py
Descripción: contrato automático estático del esquema MariaDB del Sprint 0.
Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
Fecha: 2026-10-07
Autor: Joan Catala Sendra
Aportación: verifica tablas, claves, restricciones, índices y catálogo mínimo.
"""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
schema = (ROOT / "schema.sql").read_text(encoding="utf-8")
seed = (ROOT / "seed.sql").read_text(encoding="utf-8")

required_schema = [
    "CREATE TABLE IF NOT EXISTS Dispositivo",
    "CREATE TABLE IF NOT EXISTS TipoMedida",
    "CREATE TABLE IF NOT EXISTS Medida",
    "uuid CHAR(16) NOT NULL",
    "FOREIGN KEY (dispositivoId)",
    "REFERENCES Dispositivo(dispositivoId)",
    "FOREIGN KEY (tipoMedidaId)",
    "REFERENCES TipoMedida(tipoMedidaId)",
    "CHECK (contador BETWEEN 0 AND 255)",
    "CHECK (valor BETWEEN -32768 AND 65535)",
    "INDEX idx_medida_dispositivo_tipo_fecha",
    "INDEX idx_medida_tipo_fecha",
]

required_seed = [
    "EPSG-GTI-PROY-3A",
    "GTI Joan",
    "(12, 'Temperatura', '°C')",
    "(14, 'O3', 'ppb')",
]

for expected in required_schema:
    assert expected in schema, f"Falta en schema.sql: {expected}"

for expected in required_seed:
    assert expected in seed, f"Falta en seed.sql: {expected}"

assert "'CO2'" not in seed, "El Sprint 0 no debe sembrar tipos no utilizados"
assert "'Ruido'" not in seed, "El Sprint 0 no debe sembrar tipos no utilizados"

print("DATABASE CONTRACT TEST: OK")
