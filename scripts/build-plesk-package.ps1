# Archivo: build-plesk-package.ps1
# Descripción: genera un paquete de despliegue Plesk sin duplicar código versionado.
# Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
# Fecha: 2026-10-07
# Autor: Joan Catala Sendra
# Aportación: empaquetado reproducible de GUI, proxy web, comunicación y lógica.

$ErrorActionPreference = "Stop"
$Repo = Split-Path -Parent $PSScriptRoot
$Destino = Join-Path $Repo ".dist\plesk\biometria"

if (Test-Path $Destino) {
    Remove-Item $Destino -Recurse -Force
}

New-Item -ItemType Directory -Path $Destino | Out-Null
New-Item -ItemType Directory -Path (Join-Path $Destino "css") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $Destino "js") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $Destino "server") | Out-Null

Copy-Item (Join-Path $Repo "src\gui\css\styles.css") (Join-Path $Destino "css\styles.css")
Copy-Item (Join-Path $Repo "src\gui\js\app.js") (Join-Path $Destino "js\app.js")
Copy-Item (Join-Path $Repo "src\frontend_business_logic\web\LogicaFake.js") (Join-Path $Destino "js\LogicaFake.js")
Copy-Item (Join-Path $Repo "src\communication\api.php") (Join-Path $Destino "api.php")
Copy-Item (Join-Path $Repo "src\business_logic\Logica.php") (Join-Path $Destino "server\Logica.php")
Copy-Item (Join-Path $Repo "src\business_logic\SDBaseDatos.example.php") (Join-Path $Destino "server\SDBaseDatos.example.php")

$Index = Get-Content (Join-Path $Repo "src\gui\index.html") -Raw
$Index = $Index.Replace(
    "../frontend_business_logic/web/LogicaFake.js",
    "js/LogicaFake.js"
)
Set-Content -Path (Join-Path $Destino "index.html") -Value $Index -Encoding UTF8

Write-Host "Paquete generado en: $Destino" -ForegroundColor Green
Write-Host "Cree server/SDBaseDatos.php SOLO en Plesk; no lo añada al repositorio." -ForegroundColor Yellow
