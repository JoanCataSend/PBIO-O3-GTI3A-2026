from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
publicador = (ROOT / "Publicador.h").read_text(encoding="utf-8")
medidor = (ROOT / "Medidor.h").read_text(encoding="utf-8")

checks = {
    "ID O3 = 14": "O3 = 14" in publicador,
    "ID temperatura = 12": "TEMPERATURA = 12" in publicador,
    "Major empaqueta ID y contador": "((uint16_t)idMedida << 8) | contador" in publicador,
    "O3 fake = 123": "const int16_t valorO3 = 123" in medidor,
    "Temperatura fake = -12": "const int16_t valorTemperatura = -12" in medidor,
    "UUID del proyecto presente": all(token in publicador for token in ["'E'", "'P'", "'S'", "'G'", "'3'", "'A'"]),
}

failed = [name for name, ok in checks.items() if not ok]
for name, ok in checks.items():
    print(f"[{'OK' if ok else 'FALLO'}] {name}")

assert not failed, "Incumplimientos firmware: " + ", ".join(failed)
assert ((14 << 8) | 16) == 3600
assert ((12 << 8) | 16) == 3088
print("FIRMWARE CONTRACT TEST: OK")
