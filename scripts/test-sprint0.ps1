<#
 Archivo: test-sprint0.ps1
 Descripción: ejecuta los tests locales reproducibles del Sprint 0 con la arquitectura del Agente v2.
 Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 Fecha: 2026-10-07
 Autor: Joan Catala Sendra
 Aportación: ejecución agrupada de auditoría, contratos, lógica, proxy frontend, GUI y Android.
#>

param(
    [switch]$Integracion,
    [switch]$SinAndroid
)

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot

<#
--------------------
Diseño lógico: titulo: Text --> Ejecutar()
Descripción: ejecuta una prueba y detiene el script si el proceso devuelve error.
Nota: el bloque ejecutable es un mecanismo de PowerShell y se omite del diseño lógico.
--------------------
#>
function Ejecutar($titulo, $comando) {
    Write-Host "`n=== $titulo ===" -ForegroundColor Cyan
    & $comando
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Push-Location $raiz
try {
    Ejecutar "Auditoría Agente v2" { python scripts/audit-repository.py }
    Ejecutar "Contrato firmware" { python src/firmware/tests/firmware_contract_test.py }
    Ejecutar "Contrato base de datos" { python src/database/tests/schema_contract_test.py }
    Ejecutar "Proxy frontend web" { node src/frontend_business_logic/tests/web_proxy_test.js }
    Ejecutar "GUI web" { node src/gui/tests/gui_unit_test.js }

    $php = Get-Command php -ErrorAction SilentlyContinue

    if ($php) {
        $phpExe = $php.Source
    }
    elseif (Test-Path "C:\xampp\php\php.exe") {
        $phpExe = "C:\xampp\php\php.exe"
    }
    else {
        $phpExe = $null
    }

    if ($phpExe) {
        Ejecutar "Sintaxis lógica PHP" { & $phpExe -l src/business_logic/Logica.php }
        Ejecutar "Sintaxis comunicación PHP" { & $phpExe -l src/communication/api.php }
        Ejecutar "Sintaxis tests PHP" { & $phpExe -l src/business_logic/tests/LogicaUnitTest.php }
        Ejecutar "Lógica de negocio PHP" { & $phpExe src/business_logic/tests/LogicaUnitTest.php }
    }
    else {
        Write-Host "`n[AVISO] PHP no está disponible localmente; se omiten las pruebas PHP." -ForegroundColor Yellow
    }

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
        if (-not $php) {
            throw "La integración remota requiere PHP en PATH o C:\xampp\php\php.exe"
        }
        Ejecutar "Comunicación desplegada" { & $php.Source src/communication/tests/ApiIntegracionTest.php }
    }

    Write-Host "`nTests seleccionados finalizados correctamente." -ForegroundColor Green
    if (-not $Integracion) {
        Write-Host "La integración remota no se ha ejecutado. Use -Integracion cuando el servidor esté accesible."
    }
}
finally {
    Pop-Location
}
