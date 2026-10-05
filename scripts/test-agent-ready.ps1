$ErrorActionPreference = "Stop"

Write-Host "=== 1. Estructura AGENTS.md ==="
python scripts\audit-agent-ready.py

Write-Host "`n=== 2. Contrato firmware ==="
python src\firmware\tests\firmware_contract_test.py

Write-Host "`n=== 3. Contrato base de datos ==="
python src\database\tests\schema_contract_test.py

Write-Host "`n=== 4. Lógica PHP ==="
C:\xampp\php\php.exe src\business_logic\tests\LogicaUnitTest.php

Write-Host "`n=== 5. Web ==="
node src\web\tests\web_unit_test.js

Write-Host "`n=== 6. API integración HTTPS ==="
C:\xampp\php\php.exe src\api_rest\tests\ApiIntegracionTest.php

Write-Host "`n=== 7. Android ==="
Push-Location src\android
.\gradlew.bat test
Pop-Location

Write-Host "`nTODOS LOS TESTS DISPONIBLES HAN FINALIZADO."
