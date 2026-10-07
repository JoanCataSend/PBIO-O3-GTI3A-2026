<#
 Archivo: test-sprint0.ps1
 Descripción: ejecuta los tests locales reproducibles del Sprint 0 en Windows.
 Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 Fecha: 2026-10-07
 Autor: Joan Catala Sendra
 Aportación: ejecución agrupada de auditoría, contratos, lógica, web y Android.
#>

param(
    [switch]$Integracion,
    [switch]$SinAndroid
)

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot

function Ejecutar($titulo, $comando) {
    Write-Host "`n=== $titulo ===" -ForegroundColor Cyan
    & $comando
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Push-Location $raiz
try {
    Ejecutar "Auditoría estructural" { python scripts/audit-repository.py }
    Ejecutar "Contrato firmware" { python src/firmware/tests/firmware_contract_test.py }
    Ejecutar "Contrato base de datos" { python src/database/tests/schema_contract_test.py }

    $php = Get-Command php -ErrorAction SilentlyContinue
    if (-not $php -and (Test-Path "C:\xampp\php\php.exe")) {
        $php = Get-Item "C:\xampp\php\php.exe"
    }
    if (-not $php) {
        throw "PHP no está disponible en PATH ni en C:\xampp\php\php.exe"
    }

    Ejecutar "Lógica de negocio PHP" { & $php.Source src/business_logic/tests/LogicaUnitTest.php }
    Ejecutar "Web JavaScript" { node src/web/tests/web_unit_test.js }

    if (-not $SinAndroid) {
        Write-Host "`n=== Android unit tests ===" -ForegroundColor Cyan
        Push-Location src/android
        try {
            & .\gradlew.bat test
            if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        }
        finally {
            Pop-Location
        }
    }

    if ($Integracion) {
        Ejecutar "API desplegada" { & $php.Source src/api_rest/tests/ApiIntegracionTest.php }
    }

    Write-Host "`nTests seleccionados finalizados correctamente." -ForegroundColor Green
    if (-not $Integracion) {
        Write-Host "La integración remota no se ha ejecutado. Use -Integracion cuando el servidor esté accesible."
    }
}
finally {
    Pop-Location
}
