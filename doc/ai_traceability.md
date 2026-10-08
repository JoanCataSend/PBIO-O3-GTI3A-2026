# Trazabilidad de uso de IA - Sprint 0

La trazabilidad sigue el flujo requerido por la rúbrica: **diseño previo -> prompt -> implementación -> revisión**.

| Trabajo | Diseño de referencia | Prompt | Implementación resultante/revisada |
|---|---|---|---|
| Base de datos | `database_design.md` | `prompts/01_database.md` | `src/database/` |
| Lógica de negocio servidor | `business_logic_design.md` | `prompts/02_business_logic.md` | `src/business_logic/` |
| Comunicación REST | `communication_design.md` | `prompts/03_api_rest.md` | `src/communication/` |
| Proxy Android | `frontend_business_logic_design.md` + `android_design.md` | `prompts/04_android_fake.md` | `src/frontend_business_logic/android/` |
| Proxy web | `frontend_business_logic_design.md` | `prompts/05_web_fake.md` | `src/frontend_business_logic/web/` |
| GUI web | `gui_design.md` | `prompts/06_web_ux.md` | `src/gui/` |

La versión final se revisa además con `scripts/audit-repository.py`, los tests por componente y la prueba presencial extremo a extremo.
