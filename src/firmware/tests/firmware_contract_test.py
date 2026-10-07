"""
Archivo: firmware_contract_test.py
Descripción: contrato automático estático del firmware y del protocolo iBeacon.
Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
Fecha: 2026-10-07
Autor: Joan Catala Sendra
Aportación: comprueba constantes, valores ficticios y empaquetado Major/Minor.
"""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "NodoO3"
publicador = (ROOT / "Publicador.h").read_text(encoding="utf-8")
medidor = (ROOT / "Medidor.h").read_text(encoding="utf-8")

checks = {
    "ID O3 = 14": "O3 = 14" in publicador,
    "ID temperatura = 12": "TEMPERATURA = 12" in publicador,
    "sin IDs no utilizados": "CO2" not in publicador and "RUIDO" not in publicador,
    "Major empaqueta ID y contador": "((uint16_t)idMedida << 8) | contador" in publicador,
    "O3 fake = 123 y natural de 16 bits": "const uint16_t valorO3 = 123" in medidor and "uint16_t medirO3()" in medidor,
    "Temperatura fake = -12": "const int16_t valorTemperatura = -12" in medidor,
    "UUID de 16 bytes presente": "'E', 'P', 'S', 'G'" in publicador and "'3', 'A'" in publicador,
}

failed = [name for name, ok in checks.items() if not ok]
for name, ok in checks.items():
    print(f"[{'OK' if ok else 'FALLO'}] {name}")

assert not failed, "Incumplimientos firmware: " + ", ".join(failed)
assert ((14 << 8) | 16) == 3600
assert ((12 << 8) | 16) == 3088
assert (0xFFF4 - 0x10000) == -12
assert 0 <= 65535 <= 0xFFFF

print("FIRMWARE CONTRACT TEST: OK")
