# Código fuente

`src/` es la **única fuente de verdad** de la implementación del Sprint 0. No existe una segunda copia «operativa» del código en la raíz.

Cada subcarpeta corresponde uno-a-uno con un diseño `doc/xxx_design.md`, tal como exige el agente de revisión:

| Diseño | Implementación |
|---|---|
| `doc/firmware_design.md` | `src/firmware/` |
| `doc/android_design.md` | `src/android/` |
| `doc/database_design.md` | `src/database/` |
| `doc/business_logic_design.md` | `src/business_logic/` |
| `doc/api_rest_design.md` | `src/api_rest/` |
| `doc/web_design.md` | `src/web/` |

Los tests de cada componente están junto a su implementación cuando procede.
