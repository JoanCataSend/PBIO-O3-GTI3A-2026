from pathlib import Path
import re
import sys
import subprocess

ROOT = Path(__file__).resolve().parents[1]
errors = []
passed = []


def check(condition, ok, fail):
    (passed if condition else errors).append(ok if condition else fail)

check((ROOT / "author.md").is_file(), "author.md existe", "Falta author.md")
if (ROOT / "author.md").is_file():
    check(bool((ROOT / "author.md").read_text(encoding="utf-8").strip()), "author.md contiene autor", "author.md está vacío")

for folder in ("doc", "src"):
    check((ROOT / folder).is_dir(), f"{folder}/ existe", f"Falta {folder}/")

if (ROOT / "doc").is_dir():
    designs = sorted((ROOT / "doc").glob("*_design.md"))
    check(len(designs) >= 1, "Existen xxx_design.md", "No hay xxx_design.md")
    for design in designs:
        component = design.name[:-len("_design.md")]
        text = design.read_text(encoding="utf-8")
        check((ROOT / "src" / component).is_dir(),
              f"{design.name} -> src/{component}/",
              f"No existe src/{component}/ para {design.name}")
        for heading in ("Component Design", "Design Clarifications", "General Rules"):
            check(heading in text,
                  f"{design.name}: sección {heading}",
                  f"{design.name}: falta sección {heading}")
        for rule in ("Programming Language", "Function/Method Headers", "Code Readability", "Automated Testing"):
            check(rule in text,
                  f"{design.name}: regla {rule}",
                  f"{design.name}: falta regla {rule}")

source_exts = {".java", ".php", ".js", ".h", ".ino"}
for p in (ROOT / "src").rglob("*"):
    if not p.is_file() or p.suffix not in source_exts:
        continue
    text = p.read_text(encoding="utf-8", errors="replace")
    for m in re.finditer(r"/\*\*?.*?\*/", text, flags=re.S):
        block = m.group(0)
        if "Diseño lógico:" in block:
            check(block.count("--------------------") >= 2,
                  f"{p.relative_to(ROOT)}: diseño lógico delimitado",
                  f"{p.relative_to(ROOT)}: diseño lógico sin doble delimitador")


# Correspondencia exacta entre el árbol operativo y la vista canónica del revisor.
def same_file(a: Path, b: Path) -> bool:
    return a.is_file() and b.is_file() and a.read_bytes() == b.read_bytes()

# Archivos/directorios locales o generados que nunca forman parte del repositorio entregable.
LOCAL_ONLY_DIRS = {".gradle", ".idea", "build", ".cxx", ".externalNativeBuild", "captures"}
LOCAL_ONLY_FILES = {"local.properties", ".DS_Store", "Thumbs.db", "desktop.ini"}

def is_local_only(rel: Path) -> bool:
    if any(part in LOCAL_ONLY_DIRS for part in rel.parts):
        return True
    if rel.name in LOCAL_ONLY_FILES or rel.suffix == ".iml":
        return True
    return False

def check_tree(operational: Path, canonical: Path, label: str):
    for source in operational.rglob("*"):
        if not source.is_file():
            continue
        rel = source.relative_to(operational)
        if is_local_only(rel):
            continue
        target = canonical / rel
        check(same_file(source, target),
              f"Mirror {label}: {rel}",
              f"Mirror {label} no coincide: {rel}")

check_tree(ROOT / "firmware" / "NodoO3", ROOT / "src" / "firmware", "firmware")
check_tree(ROOT / "android", ROOT / "src" / "android", "android")
check_tree(ROOT / "server" / "database", ROOT / "src" / "database", "database")
check_tree(ROOT / "web" / "css", ROOT / "src" / "web" / "css", "web/css")
check_tree(ROOT / "web" / "js", ROOT / "src" / "web" / "js", "web/js")
check(same_file(ROOT / "web" / "index.html", ROOT / "src" / "web" / "index.html"),
      "Mirror web: index.html", "Mirror web no coincide: index.html")
check(same_file(ROOT / "server" / "Logica.php", ROOT / "src" / "business_logic" / "Logica.php"),
      "Mirror business_logic: Logica.php", "Mirror business_logic no coincide: Logica.php")
check(same_file(ROOT / "server" / "SDBaseDatos.example.php", ROOT / "src" / "business_logic" / "SDBaseDatos.example.php"),
      "Mirror business_logic: SDBaseDatos.example.php", "Mirror business_logic no coincide: SDBaseDatos.example.php")
check(same_file(ROOT / "server" / "tests" / "LogicaUnitTest.php", ROOT / "src" / "business_logic" / "tests" / "LogicaUnitTest.php"),
      "Mirror business_logic: test", "Mirror business_logic no coincide: test")
check(same_file(ROOT / "web" / "api.php", ROOT / "src" / "api_rest" / "api.php"),
      "Mirror api_rest: api.php", "Mirror api_rest no coincide: api.php")
check(same_file(ROOT / "server" / "tests" / "ApiIntegracionTest.php", ROOT / "src" / "api_rest" / "tests" / "ApiIntegracionTest.php"),
      "Mirror api_rest: test", "Mirror api_rest no coincide: test")

def is_git_ignored(path: str) -> bool:
    # En un clon de trabajo puede existir configuración privada local siempre que Git la ignore.
    # En un ZIP/checkout sin .git, cualquier archivo privado presente sí sería un fallo real.
    if not (ROOT / ".git").exists():
        return False
    result = subprocess.run(
        ["git", "check-ignore", "-q", "--", path],
        cwd=ROOT,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    return result.returncode == 0

for forbidden in ("server/SDBaseDatos.php", "android/local.properties", ".env"):
    exists = (ROOT / forbidden).exists()
    safe = (not exists) or is_git_ignored(forbidden)
    ok_msg = f"No se incluye {forbidden}" if not exists else f"{forbidden} existe solo localmente y está ignorado por Git"
    check(safe, ok_msg, f"Se ha incluido archivo privado {forbidden}")

print("=== AGENT READY AUDIT ===")
for item in passed:
    print("[OK]", item)
if errors:
    print("\n=== FAILED ===")
    for item in errors:
        print("[FALLO]", item)
    sys.exit(1)
print(f"\nRESULTADO: {len(passed)}/{len(passed)} comprobaciones estructurales correctas.")
print("AGENT READY AUDIT: OK")
