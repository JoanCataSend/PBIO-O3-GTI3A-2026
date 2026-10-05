from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
schema = (ROOT / "schema.sql").read_text(encoding="utf-8")
seed = (ROOT / "seed.sql").read_text(encoding="utf-8")

required_schema = [
    "CREATE TABLE IF NOT EXISTS Dispositivo",
    "CREATE TABLE IF NOT EXISTS TipoMedida",
    "CREATE TABLE IF NOT EXISTS Medida",
    "FOREIGN KEY (dispositivoId)",
    "REFERENCES Dispositivo(dispositivoId)",
    "FOREIGN KEY (tipoMedidaId)",
    "REFERENCES TipoMedida(tipoMedidaId)",
    "CHECK (contador BETWEEN 0 AND 255)",
    "INDEX idx_dispositivo_fecha",
    "INDEX idx_tipo_fecha",
]
required_seed = [
    "EPSG-GTI-PROY-3A",
    "GTI Joan",
    "(11, 'CO2', 'ppm')",
    "(12, 'Temperatura', '°C')",
    "(13, 'Ruido', 'dB')",
    "(14, 'O3', 'ppb')",
]

for text in required_schema:
    assert text in schema, f"Falta en schema.sql: {text}"
for text in required_seed:
    assert text in seed, f"Falta en seed.sql: {text}"

print("DATABASE CONTRACT TEST: OK")
