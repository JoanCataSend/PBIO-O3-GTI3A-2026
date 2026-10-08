#!/usr/bin/env python3
"""
Archivo: audit-repository.py
Descripción: auditoría estructural reproducible contra AGENTS v2, rúbrica y notación oficial v3.
Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
Fecha: 2026-10-07
Autor: Joan Catala Sendra
Aportación: comprueba tríada backend, proxy frontend, desacoplamiento, paridad lógica y documentación.
"""

from pathlib import Path
import re
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
COMPONENTS = (
    "firmware",
    "android",
    "communication",
    "business_logic",
    "database",
    "frontend_business_logic",
    "gui",
)
PROMPTS = (
    "01_database.md",
    "02_business_logic.md",
    "03_api_rest.md",
    "04_android_fake.md",
    "05_web_fake.md",
    "06_web_ux.md",
)
TEXT_EXTENSIONS = {
    ".ino", ".h", ".java", ".php", ".js", ".sql", ".html", ".css", ".py",
    ".md", ".gradle", ".ps1", ".pro", ".properties", ".xml"
}
HEADER_FIELDS = ("Archivo:", "Descripción:", "Copyright:", "Fecha:", "Autor:", "Aportación:")
SKIP_PARTS = {".git", ".gradle", ".idea", "build", ".cxx", ".externalNativeBuild", "captures", ".dist"}
AUTHORED_CONFIGS = {
    "src/android/app/build.gradle",
    "src/android/build.gradle",
    "src/android/settings.gradle",
    "src/android/gradle.properties",
    "src/android/app/proguard-rules.pro",
    "src/android/app/src/main/AndroidManifest.xml",
    "src/android/app/src/main/res/layout/activity_main.xml",
    "src/android/app/src/main/res/values/colors.xml",
    "src/android/app/src/main/res/values/strings.xml",
    "src/android/app/src/main/res/values/themes.xml",
    "src/android/app/src/main/res/values-night/themes.xml",
    "scripts/test-sprint0.ps1",
    "scripts/build-plesk-package.ps1",
}
DOMAIN_PARITY_SIGNATURES = (
    "probarConexion() --> estado: EstadoBD",
    "datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista",
    "filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]",
    "listarDispositivos() --> dispositivos: [Dispositivo]",
    "listarTiposMedida() --> tipos: [TipoMedida]",
)

oks: list[str] = []
errors: list[str] = []


# --------------------
# Diseño lógico: condition: B, ok: Text, fail: Text --> check()
# Descripción: registra una comprobación como correcta o fallida.
# --------------------
def check(condition: bool, ok: str, fail: str) -> None:
    (oks if condition else errors).append(ok if condition else fail)


# --------------------
# Diseño lógico: path: Text --> relative() --> ruta: Text
# Descripción: expresa una ruta del repositorio de forma relativa y estable.
# --------------------
def relative(path: Path) -> str:
    return path.relative_to(ROOT).as_posix()


# --------------------
# Diseño lógico: path: Text --> is_skipped() --> omitido: B
# Descripción: indica si una ruta pertenece a un directorio generado o local.
# --------------------
def is_skipped(path: Path) -> bool:
    return any(part in SKIP_PARTS for part in path.relative_to(ROOT).parts)


# --------------------
# Diseño lógico: path: Text --> is_tracked() --> tracked: B
# Descripción: indica si Git versiona el archivo; sin .git usa existencia como aproximación conservadora.
# --------------------
def is_tracked(path: Path) -> bool:
    if not path.exists():
        return False
    if not (ROOT / ".git").is_dir():
        return True
    result = subprocess.run(
        ["git", "ls-files", "--error-unmatch", "--", relative(path)],
        cwd=ROOT,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        check=False,
    )
    return result.returncode == 0


# --------------------
# Diseño lógico: texto: Text --> normalize_ws() --> normalizado: Text
# Descripción: elimina diferencias de espacios para comparar firmas lógicas.
# --------------------
def normalize_ws(texto: str) -> str:
    return re.sub(r"\s+", " ", texto).strip()


# 1) Estructura de repositorio y autor.
check((ROOT / "README.md").is_file(), "README.md existe", "Falta README.md")
check((ROOT / "author.md").is_file(), "author.md existe", "Falta author.md")
check((ROOT / "doc").is_dir(), "doc/ existe", "Falta doc/")
check((ROOT / "src").is_dir(), "src/ existe", "Falta src/")

if (ROOT / "author.md").is_file():
    author = (ROOT / "author.md").read_text(encoding="utf-8")
    check("Joan Catala Sendra" in author, "author.md identifica al autor", "author.md no identifica a Joan Catala Sendra")

for duplicate in ("android", "firmware", "server", "web", "api_rest", "docs"):
    check(not (ROOT / duplicate).exists(), f"No existe copia raíz {duplicate}/", f"Carpeta duplicada/legacy en raíz: {duplicate}/")

# 2) Correspondencia doc/xxx_design.md <-> src/xxx/ y formato de diseños.
designs = sorted((ROOT / "doc").glob("*_design.md")) if (ROOT / "doc").is_dir() else []
design_names = {p.stem.removesuffix("_design") for p in designs}
check(
    design_names == set(COMPONENTS),
    "Los siete componentes tienen diseño y no hay diseños huérfanos",
    f"Diseños encontrados: {sorted(design_names)}; esperados: {sorted(COMPONENTS)}",
)

for component in COMPONENTS:
    design = ROOT / "doc" / f"{component}_design.md"
    source = ROOT / "src" / component
    check(design.is_file(), f"Existe {relative(design)}", f"Falta {relative(design)}")
    check(source.is_dir(), f"Existe {relative(source)}/", f"Falta {relative(source)}/")
    if not design.is_file():
        continue
    text = design.read_text(encoding="utf-8")
    for heading in ("## Diseño del Componente", "## Aclaraciones del Diseño", "## Reglas Generales"):
        check(heading in text, f"{design.name}: {heading}", f"{design.name}: falta {heading}")
    for rule in ("Lenguaje", "Encabezados de Funciones/Métodos", "Legibilidad del Código", "Pruebas Automatizadas"):
        check(rule in text, f"{design.name}: regla {rule}", f"{design.name}: falta regla {rule}")
    for label, pattern in {
        "Texto": r"\bTexto\b",
        "VoF": r"\bVoF\b",
        "VACIO": r"\bVACIO\b",
        "Json": r"\bJson\b",
        "MedidaVistaBD": r"\bMedidaVistaBD\b",
        "| Error": r"\|\s*Error\b",
    }.items():
        check(re.search(pattern, text) is None, f"{design.name}: sin tipo no oficial '{label}'", f"{design.name}: usa tipo/notación no oficial '{label}'")

# 3) Requisitos nuevos del agente v2: tríada backend y proxy frontend.
for required in (
    ROOT / "doc/communication_design.md",
    ROOT / "doc/business_logic_design.md",
    ROOT / "doc/database_design.md",
    ROOT / "doc/frontend_business_logic_design.md",
    ROOT / "src/communication",
    ROOT / "src/business_logic",
    ROOT / "src/database",
    ROOT / "src/frontend_business_logic",
    ROOT / "src/gui",
):
    check(required.exists(), f"Agente v2: existe {relative(required)}", f"Agente v2: falta {relative(required)}")

# El antiguo particionado no debe competir con la tríada v2.
for old in (ROOT / "doc/api_rest_design.md", ROOT / "doc/web_design.md", ROOT / "src/api_rest", ROOT / "src/web"):
    check(not old.exists(), f"Sin componente legacy {relative(old)}", f"Componente legacy incompatible con agente v2: {relative(old)}")

# 4) Formato estricto de base de datos.
db_design = ROOT / "doc/database_design.md"
if db_design.is_file():
    db_text = db_design.read_text(encoding="utf-8")
    for token in ("TABLE:", "DESCRIPTION:", "COLUMNS:", "PRIMARY KEY:", "FOREIGN KEYS:", "CONSTRAINTS:"):
        check(token in db_text, f"database_design: contiene {token}", f"database_design: falta {token}")
    tables = re.findall(r"^TABLE:\s*([A-Za-z_][A-Za-z0-9_]*)\s*$", db_text, flags=re.M)
    check(tables == ["Dispositivo", "TipoMedida", "Medida"], "database_design: define exactamente las tres tablas del esquema", f"database_design: tablas detectadas {tables}")

# 5) Independencia estricta de business_logic y consciencia de BD.
bl_design = ROOT / "doc/business_logic_design.md"
if bl_design.is_file():
    bl_text = bl_design.read_text(encoding="utf-8")
    forbidden_design = r"\b(HttpRequest|HttpResponse|HTTP|REST|WebSocket|socket|routing|header|cabecera|status code|código de estado|fetch)\b"
    check(re.search(forbidden_design, bl_text, flags=re.I) is None,
          "business_logic_design: 100% independiente de transporte",
          "business_logic_design: contiene conceptos de comunicación/transporte")
    for entity in ("Dispositivo", "TipoMedida", "Medida"):
        check(entity in bl_text, f"business_logic_design: referencia entidad BD {entity}", f"business_logic_design: no referencia {entity}")
    for column in ("dispositivoId", "uuid", "nombre", "tipoMedidaId", "medidaId", "valor", "contador", "rssi", "fechaHora"):
        check(column in bl_text, f"business_logic_design: alinea columna {column}", f"business_logic_design: no alinea columna {column}")

bl_source_files = list((ROOT / "src/business_logic").rglob("*")) if (ROOT / "src/business_logic").is_dir() else []
bl_source = "\n".join(
    p.read_text(encoding="utf-8", errors="ignore")
    for p in bl_source_files if p.is_file() and p.suffix.lower() in TEXT_EXTENSIONS and not is_skipped(p)
)
check(re.search(r"src[/\\]communication|HttpRequest|HttpResponse|HttpURLConnection|fetch\s*\(|WebSocket|curl_|https?://", bl_source, flags=re.I) is None,
      "src/business_logic: cero dependencias de comunicación",
      "src/business_logic: contiene dependencia de comunicación")

# 6) Comunicación depende de lógica, no contiene SQL de dominio.
communication_source = ROOT / "src/communication/api.php"
if communication_source.is_file():
    comm_text = communication_source.read_text(encoding="utf-8")
    check("business_logic" in comm_text or "Logica.php" in comm_text,
          "communication invoca/carga business_logic",
          "communication no muestra dependencia hacia business_logic")
    check(re.search(r"\b(SELECT|INSERT|UPDATE|DELETE)\b", comm_text, flags=re.I) is None,
          "communication no contiene SQL",
          "communication contiene SQL y mezcla responsabilidades")

# 7) GUI desacoplada de transporte.
gui_files = [
    ROOT / "src/gui/index.html",
    ROOT / "src/gui/js/app.js",
]
gui_source = "\n".join(
    p.read_text(encoding="utf-8", errors="ignore")
    for p in gui_files if p.is_file()
)
check(re.search(r"fetch\s*\(|XMLHttpRequest|WebSocket|HttpURLConnection|https?://", gui_source, flags=re.I) is None,
      "src/gui: sin comunicación HTTP/fetch/WebSocket directa",
      "src/gui: contiene comunicación de transporte directa")
check("frontend_business_logic" in gui_source or "LogicaFake" in gui_source,
      "src/gui consume frontend_business_logic",
      "src/gui no muestra consumo del proxy frontend")

android_main = ROOT / "src/android/app/src/main/java/es/upv/jcatsen/pbio/MainActivity.java"
if android_main.is_file():
    android_text = android_main.read_text(encoding="utf-8")
    check(re.search(r"PeticionarioREST|HttpURLConnection|JSONObject|https?://", android_text) is None,
          "GUI Android no conoce transporte ni JSON",
          "GUI Android conoce detalles de transporte/JSON")
    check("LogicaFake" in android_text, "GUI Android consume LogicaFake", "GUI Android no consume frontend_business_logic")

# 8) Paridad de interfaz frontend/backend.
frontend_design = ROOT / "doc/frontend_business_logic_design.md"
if frontend_design.is_file() and bl_design.is_file():
    front_norm = normalize_ws(frontend_design.read_text(encoding="utf-8"))
    back_norm = normalize_ws(bl_design.read_text(encoding="utf-8"))
    for signature in DOMAIN_PARITY_SIGNATURES:
        normalized = normalize_ws(signature)
        check(normalized in front_norm, f"frontend declara firma: {signature}", f"frontend no declara firma: {signature}")
        check(normalized in back_norm, f"backend declara firma homóloga: {signature}", f"backend no declara firma homóloga: {signature}")

# 9) Exactamente seis prompts académicos y actualizados a la arquitectura v2.
prompt_dir = ROOT / "doc/prompts"
prompt_files = tuple(sorted(p.name for p in prompt_dir.glob("[0-9][0-9]_*.md"))) if prompt_dir.is_dir() else ()
check(prompt_files == PROMPTS, "Existen exactamente los seis prompts exigidos", f"Prompts encontrados: {prompt_files}")
if prompt_dir.is_dir():
    prompts_combined = "\n".join(p.read_text(encoding="utf-8") for p in prompt_dir.glob("[0-9][0-9]_*.md"))
    for target in ("src/communication", "src/business_logic", "src/database", "src/frontend_business_logic", "src/gui"):
        check(target in prompts_combined, f"Prompts cubren {target}", f"Prompts no cubren {target}")

# 10) Secretos/locales: pueden existir en la máquina, pero nunca versionados.
for forbidden in (
    ROOT / "src/business_logic/SDBaseDatos.php",
    ROOT / "src/frontend_business_logic/SDBaseDatos.php",
    ROOT / ".env",
    ROOT / "src/android/local.properties",
):
    check(not is_tracked(forbidden), f"No se versiona {relative(forbidden)}", f"Archivo privado versionado: {relative(forbidden)}")

# 11) Inventario textual excluyendo build/IDE/generados.
text_files: list[tuple[Path, str]] = []
for path in ROOT.rglob("*"):
    if not path.is_file() or is_skipped(path) or path.suffix.lower() not in TEXT_EXTENSIONS:
        continue
    try:
        text_files.append((path, path.read_text(encoding="utf-8")))
    except UnicodeDecodeError:
        pass

source_combined = "\n".join(text for path, text in text_files if "src" in path.relative_to(ROOT).parts)
check("org.jordi.prueba2025" not in source_combined, "No queda el package heredado en src/", "Queda el package heredado en src/")
check("Prueba2025" not in source_combined, "No queda el nombre de proyecto heredado en src/", "Queda Prueba2025 en src/")
check("TODO:" not in source_combined and "FIXME" not in source_combined, "No quedan TODO/FIXME en src/", "Quedan TODO/FIXME en src/")

# 12) Cabeceras de ficheros de autoría propia.
for path, text in text_files:
    rel = relative(path)
    authored_code = path.suffix.lower() in {".ino", ".h", ".java", ".php", ".js", ".sql", ".html", ".css", ".py"}
    if authored_code or rel in AUTHORED_CONFIGS:
        if "/gradle/wrapper/" in rel or "/mipmap-" in rel or "/drawable" in rel:
            continue
        for field in HEADER_FIELDS:
            check(field in text, f"{rel}: cabecera {field}", f"{rel}: falta {field}")

# 13) Bloques lógicos delimitados, tipos oficiales y flechas matemáticas correctas.
for path, text in text_files:
    if path.suffix.lower() not in {".ino", ".h", ".java", ".php", ".js", ".py", ".ps1"}:
        continue
    if "Diseño lógico:" not in text:
        continue
    for block in re.findall(r"/\*\*?.*?\*/|<#.*?#>|\"\"\".*?\"\"\"", text, flags=re.S):
        if "Diseño lógico:" not in block:
            continue
        rel = relative(path)
        check(block.count("--------------------") >= 2,
              f"{rel}: bloque lógico delimitado",
              f"{rel}: bloque Diseño lógico sin dos delimitadores")
        check(re.search(r"\b(Json|Texto|VoF|VACIO|MedidaVistaBD)\b", block) is None,
              f"{rel}: tipos lógicos oficiales",
              f"{rel}: bloque lógico usa tipo no oficial")
        logical_part = block.split("Diseño lógico:", 1)[1].split("Descripción:", 1)[0]
        logical_lines = [line.strip(" *\t\r") for line in logical_part.splitlines() if line.strip(" *\t\r")]
        logical_signature = " ".join(logical_lines)
        check(not logical_signature.rstrip().endswith("-->"),
              f"{rel}: sin flecha de salida huérfana",
              f"{rel}: firma sin salida conserva '-->' final")

# 14) Reglas puntuales de la entrega.
check("es.upv.jcatsen.pbio" in source_combined, "Package final Android presente", "No se encuentra package Android final")
check((ROOT / "doc/acceptance_test.md").is_file(), "Existe test de aceptación documentado", "Falta doc/acceptance_test.md")
check((ROOT / "doc/ai_traceability.md").is_file(), "Existe trazabilidad IA documentada", "Falta doc/ai_traceability.md")
check(not (ROOT / "src/android/app/src/main/res/xml/backup_rules.xml").exists(), "Sin plantilla backup_rules.xml innecesaria", "Queda backup_rules.xml de plantilla")
check(not (ROOT / "src/android/app/src/main/res/xml/data_extraction_rules.xml").exists(), "Sin plantilla data_extraction_rules.xml innecesaria", "Queda data_extraction_rules.xml de plantilla")

# 15) README debe explicar arquitectura v2, despliegue y tests.
readme = (ROOT / "README.md").read_text(encoding="utf-8") if (ROOT / "README.md").is_file() else ""
for required_text in ("communication", "business_logic", "database", "frontend_business_logic", "gui", "Cómo desplegar", "Tests automáticos"):
    check(required_text.lower() in readme.lower(), f"README documenta {required_text}", f"README no documenta {required_text}")

print("=== AUDITORÍA ESTRUCTURAL AGENTE V2 / SPRINT 0 ===")
for item in oks:
    print("[OK]", item)

if errors:
    print("\n=== FALLOS ===")
    for item in errors:
        print("[FALLO]", item)
    print(f"\nResultado: {len(errors)} fallo(s), {len(oks)} comprobaciones correctas.")
    sys.exit(1)

print(f"\nResultado: {len(oks)} comprobaciones correctas; 0 fallos.")
print("Nota: esta auditoría reproduce comprobaciones objetivas del AGENTS v2; la evaluación oficial sigue siendo externa.")
