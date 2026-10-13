"""
Archivo: schema_contract_test.py
Descripción: contrato automático estático del esquema MariaDB simplificado del Sprint 0.
Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
Fecha: 2026-10-13
Autor: Joan Catala Sendra
Aportación: verifica el modelo mínimo indicado por el profesor y su semilla.
"""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
schema = (ROOT / "schema.sql").read_text(encoding="utf-8")
seed = (ROOT / "seed.sql").read_text(encoding="utf-8")

required_schema = [
    "CREATE TABLE IF NOT EXISTS Dispositivo",
    "CREATE TABLE IF NOT EXISTS TipoMedida",
    "CREATE TABLE IF NOT EXISTS Medida",
    "dispositivoId CHAR(16) NOT NULL",
    "PRIMARY KEY (dispositivoId, fechaHora)",
    "FOREIGN KEY (dispositivoId)",
    "REFERENCES Dispositivo(dispositivoId)",
    "FOREIGN KEY (tipoMedidaId)",
    "REFERENCES TipoMedida(tipoMedidaId)",
    "CHECK (valor BETWEEN -32768 AND 65535)",
]

forbidden_medida_columns = [
    "medidaId BIGINT",
    "contador TINYINT",
    "rssi SMALLINT",
]

required_seed = [
    "INSERT INTO Dispositivo (dispositivoId, nombre)",
    "EPSG-GTI-PROY-3A",
    "GTI Joan",
    "(12, 'Temperatura', '°C')",
    "(14, 'O3', 'ppb')",
]

assert schema.count("CREATE TABLE IF NOT EXISTS") == 3, (
    "El esquema debe contener exactamente tres tablas"
)

for expected in required_schema:
    assert expected in schema, f"Falta en schema.sql: {expected}"

assert "uuid CHAR(16)" not in schema, (
    "Dispositivo no debe mantener una segunda columna uuid"
)

for forbidden in forbidden_medida_columns:
    assert forbidden not in schema, (
        f"Medida contiene un campo eliminado por la simplificación: {forbidden}"
    )

for expected in required_seed:
    assert expected in seed, f"Falta en seed.sql: {expected}"

assert "'CO2'" not in seed, "El Sprint 0 no debe sembrar tipos no utilizados"
assert "'Ruido'" not in seed, "El Sprint 0 no debe sembrar tipos no utilizados"

print("DATABASE CONTRACT TEST: OK")
