# Documentación de diseño

Los documentos `*_design.md` son la especificación formal que el agente revisor compara con `src/`.

| Diseño | Implementación | Responsabilidad |
|---|---|---|
| `firmware_design.md` | `src/firmware/` | adquisición ficticia y BLE |
| `android_design.md` | `src/android/` | GUI móvil y recepción BLE |
| `communication_design.md` | `src/communication/` | entrada HTTP/JSON del backend |
| `business_logic_design.md` | `src/business_logic/` | dominio y persistencia |
| `database_design.md` | `src/database/` | esquema relacional |
| `frontend_business_logic_design.md` | `src/frontend_business_logic/` | proxy/fake de dominio para las GUI |
| `gui_design.md` | `src/gui/` | GUI web |

Todos los diseños contienen las secciones **Diseño del Componente**, **Aclaraciones del Diseño** y **Reglas Generales**. La base de datos usa además el formato estricto de tablas exigido por `Database_Design_Spec.md`.
