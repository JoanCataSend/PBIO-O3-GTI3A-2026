<#
 Archivo: test-sprint0.ps1
 Descripción: ejecuta los tests automáticos reproducibles del Sprint 0 en Windows.
 Copyright: 2026 Joan (uso académico PBIO - UPV)
 Fecha: 2026-10-01
 Autor: Joan
 Aportación: automatización local de tests PHP y Android unitarios.
#>

$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$php = "C:\xampp\php\php.exe"

if (-not (Test-Path $php)) {
    throw "No se encontró PHP de XAMPP en $php"
}

Write-Host "`n=== PHP · lógica de negocio ===" -ForegroundColor Cyan
& $php "$raiz\server\tests\LogicaUnitTest.php"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n=== PHP · integración API ===" -ForegroundColor Cyan
& $php "$raiz\server\tests\ApiIntegracionTest.php"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n=== Android · unit tests ===" -ForegroundColor Cyan
Push-Location "$raiz\android"
try {
    & .\gradlew.bat test
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
finally {
    Pop-Location
}

Write-Host "`nTests automáticos del Sprint 0 finalizados correctamente." -ForegroundColor Green
Write-Host "El test instrumentado Android se ejecuta aparte con: android\gradlew.bat connectedAndroidTest"
