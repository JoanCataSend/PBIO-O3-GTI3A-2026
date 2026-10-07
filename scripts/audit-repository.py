#!/usr/bin/env python3
"""
Archivo: audit-repository.py
Descripción: auditoría estructural reproducible contra AGENTES_es y la notación oficial.
Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
Fecha: 2026-10-07
Autor: Joan Catala Sendra
Aportación: detección de estructura ambigua, diseños incompletos y notación no oficial.
"""

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
COMPONENTS = (
    "firmware",
    "android",
    "database",
    "business_logic",
    "api_rest",
    "web",
)
PROMPTS = (
    "01_database.md",
    "02_business_logic.md",
    "03_api_rest.md",
    "04_android_fake.md",
    "05_web_fake.md",
    "06_web_ux.md",
)
SOURCE_EXTENSIONS = {".ino", ".h", ".java", ".php", ".js", ".sql", ".html", ".css", ".py"}
HEADER_FIELDS = ("Archivo:", "Descripción:", "Copyright:", "Fecha:", "Autor:", "Aportación:")

oks: list[str] = []
errors: list[str] = []


def check(condition: bool, ok: str, fail: str) -> None:
    (oks if condition else errors).append(ok if condition else fail)


# Estructura exigida por el agente.
check((ROOT / "README.md").is_file(), "README.md existe", "Falta README.md")
check((ROOT / "author.md").is_file(), "author.md existe", "Falta author.md")
check((ROOT / "doc").is_dir(), "doc/ existe", "Falta doc/")
check((ROOT / "src").is_dir(), "src/ existe", "Falta src/")

if (ROOT / "author.md").is_file():
    author = (ROOT / "author.md").read_text(encoding="utf-8")
    check("Joan Catala Sendra" in author, "author.md identifica al autor", "author.md no contiene 'Joan Catala Sendra'")

# Una sola fuente de verdad: las copias operativas antiguas no deben reaparecer.
for duplicate in ("android", "firmware", "server", "web", "docs"):
    check(not (ROOT / duplicate).exists(), f"No existe copia raíz {duplicate}/", f"Carpeta duplicada/legacy en raíz: {duplicate}/")

# Correspondencia exacta doc/xxx_design.md <-> src/xxx/.
designs = sorted((ROOT / "doc").glob("*_design.md")) if (ROOT / "doc").is_dir() else []
design_names = {p.stem.removesuffix("_design") for p in designs}
check(design_names == set(COMPONENTS), "Hay exactamente seis diseños de componentes", f"Diseños encontrados: {sorted(design_names)}")

for component in COMPONENTS:
    design = ROOT / "doc" / f"{component}_design.md"
    source = ROOT / "src" / component
    check(design.is_file(), f"Existe {design.relative_to(ROOT)}", f"Falta {design.relative_to(ROOT)}")
    check(source.is_dir(), f"Existe {source.relative_to(ROOT)}/", f"Falta {source.relative_to(ROOT)}/")
    if not design.is_file():
        continue
    text = design.read_text(encoding="utf-8")
    for heading in ("## Diseño del Componente", "## Aclaraciones del Diseño", "## Reglas Generales"):
        check(heading in text, f"{design.name}: {heading}", f"{design.name}: falta {heading}")
    for rule in ("Lenguaje", "Encabezados de Funciones/Métodos", "Legibilidad del Código", "Pruebas Automatizadas"):
        check(rule in text, f"{design.name}: regla {rule}", f"{design.name}: falta regla {rule}")
    forbidden_patterns = {
        "Texto": r"\bTexto\b",
        "VoF": r"\bVoF\b",
        "VACIO": r"\bVACIO\b",
        "| Error": r"\|\s*Error\b",
    }
    for label, pattern in forbidden_patterns.items():
        check(re.search(pattern, text) is None, f"{design.name}: sin '{label}'", f"{design.name}: usa notación no oficial '{label}'")

# Exactamente los seis prompts solicitados.
prompt_dir = ROOT / "doc" / "prompts"
prompt_files = tuple(sorted(p.name for p in prompt_dir.glob("[0-9][0-9]_*.md"))) if prompt_dir.is_dir() else ()
check(prompt_files == PROMPTS, "Existen exactamente los seis prompts exigidos", f"Prompts encontrados: {prompt_files}")

# No incluir secretos locales.
for forbidden in (
    ROOT / "src" / "business_logic" / "SDBaseDatos.php",
    ROOT / ".env",
    ROOT / "src" / "android" / "local.properties",
):
    check(not forbidden.exists(), f"No se incluye secreto/local {forbidden.relative_to(ROOT)}", f"Archivo privado incluido: {forbidden.relative_to(ROOT)}")

# No conservar identidad heredada del proyecto de ejemplo.
text_files = []
for p in ROOT.rglob("*"):
    if not p.is_file() or ".git" in p.parts:
        continue
    if p.suffix.lower() in SOURCE_EXTENSIONS | {".md", ".gradle"}:
        try:
            text_files.append((p, p.read_text(encoding="utf-8")))
        except UnicodeDecodeError:
            pass
source_combined = "\n".join(
    t for p, t in text_files
    if "src" in p.relative_to(ROOT).parts
)
check("org.jordi.prueba2025" not in source_combined, "No queda el package heredado en src/", "Queda el package org.jordi.prueba2025 en src/")
check("Prueba2025" not in source_combined, "No queda el nombre de proyecto heredado en src/", "Queda Prueba2025 en src/")

# Cabecera completa de los ficheros de código de autoría propia.
ignore_names = {"audit-repository.py"}  # se valida igualmente más abajo por contenido propio
for p, text in text_files:
    if p.suffix.lower() not in SOURCE_EXTENSIONS:
        continue
    # Recursos Android generados binarios no llegan aquí; XML de launcher generado no es código propio.
    if "src/android/app/src/main/res/drawable" in p.as_posix() or "src/android/app/src/main/res/mipmap" in p.as_posix() or "src/android/app/src/main/res/xml" in p.as_posix():
        continue
    for field in HEADER_FIELDS:
        check(field in text, f"{p.relative_to(ROOT)}: cabecera {field}", f"{p.relative_to(ROOT)}: falta {field}")

# Los bloques de diseño lógico deben estar realmente delimitados.
for p, text in text_files:
    if p.suffix.lower() not in {".ino", ".h", ".java", ".php", ".js"}:
        continue
    for match in re.finditer(r"/\*\*?.*?\*/", text, flags=re.S):
        block = match.group(0)
        if "Diseño lógico:" in block:
            check(block.count("--------------------") >= 2,
                  f"{p.relative_to(ROOT)}: bloque lógico delimitado",
                  f"{p.relative_to(ROOT)}: bloque 'Diseño lógico' sin dos delimitadores")

# Reglas puntuales del Sprint final.
check("es.upv.jcatsen.pbio" in source_combined, "Package final Android presente", "No se encuentra package Android final")
check((ROOT / "doc" / "acceptance_test.md").is_file(), "Existe test de aceptación documentado", "Falta doc/acceptance_test.md")

print("=== AUDITORÍA ESTRUCTURAL SPRINT 0 ===")
for item in oks:
    print("[OK]", item)

if errors:
    print("\n=== FALLOS ===")
    for item in errors:
        print("[FALLO]", item)
    print(f"\nResultado: {len(errors)} fallo(s), {len(oks)} comprobaciones correctas.")
    sys.exit(1)

print(f"\nResultado: {len(oks)} comprobaciones estructurales correctas; 0 fallos.")
print("Nota: esta auditoría no sustituye la revisión semántica del profesor ni el test presencial.")
