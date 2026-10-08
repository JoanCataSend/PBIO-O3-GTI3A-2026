# Criterios de aceptación reproducibles - Sprint 0

## Matriz por entregable

| Componente | Criterio reproducible | Comprobación |
|---|---|---|
| firmware | publica ID O3 14, temperatura 12, Major con ID+contador y valores ficticios | `python src/firmware/tests/firmware_contract_test.py` |
| android | decodifica UUID/Major/Minor y no contiene transporte remoto directo | `cd src/android; gradlew.bat test` + auditoría |
| frontend_business_logic | expone las mismas firmas lógicas que el subconjunto del backend y encapsula comunicación | JUnit + `node src/frontend_business_logic/tests/web_proxy_test.js` |
| communication | acepta las rutas definidas y delega en `business_logic` | `php src/communication/tests/ApiIntegracionTest.php` (contrato offline; `PBIO_API_URL` añade integración real) |
| business_logic | valida dominio, consulta catálogos y persiste sin depender de `communication` | `php src/business_logic/tests/LogicaUnitTest.php` + auditoría |
| database | esquema y semilla coinciden con el diseño formal | `python src/database/tests/schema_contract_test.py` |
| gui | no contiene `fetch`; representa datos recibidos del proxy | `node src/gui/tests/gui_unit_test.js` |
| repositorio | estructura y documentación cumplen agente v2 | `python scripts/audit-repository.py` |

## Test de funcionamiento obligatorio

1. Cambiar `O3` ficticio en `src/firmware/NodoO3/Medidor.h` a un valor reconocible.
2. Compilar y cargar el firmware.
3. Abrir Android físico y pulsar **Buscar GTI Joan**.
4. Verificar que Android muestra el mismo O3 y confirma que la medida fue guardada.
5. Abrir la GUI web desplegada.
6. Actualizar y comprobar que aparece exactamente el mismo valor.
7. Si el valor cambia en cualquiera de las capas, el test se considera fallido.

## Evidencias

Las capturas de `doc/evidencias/` pueden acompañar la defensa, pero no sustituyen la ejecución presencial.
