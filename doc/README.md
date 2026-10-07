# Documentación de diseño

Esta carpeta contiene las especificaciones que el agente de revisión debe comparar con `src/`.

| Diseño | Implementación |
|---|---|
| `firmware_design.md` | `src/firmware/` |
| `android_design.md` | `src/android/` |
| `database_design.md` | `src/database/` |
| `business_logic_design.md` | `src/business_logic/` |
| `api_rest_design.md` | `src/api_rest/` |
| `web_design.md` | `src/web/` |

Todos los `xxx_design.md` incluyen las secciones exigidas por `AGENTES_es.pdf`: **Diseño del Componente**, **Aclaraciones del Diseño** y **Reglas Generales**. La notación empleada es la de `Logical Design & Reverse Engineering Specification v3`: `N`, `Z`, `R`, `B`, `Text`, agregaciones, listas y firmas lógicas independientes del lenguaje.

`prompts/` contiene exactamente las seis especificaciones solicitadas en la segunda tarea. `evidencias/` conserva capturas de una ejecución previa, `acceptance_test.md` describe la demostración presencial reproducible y `ai_traceability.md` documenta la relación diseño → prompt → implementación → revisión.
