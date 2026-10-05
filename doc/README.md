# Design specifications for the Sprint Reviewer Agent

This directory is the **canonical review interface** required by `AGENTS.md`.
Every `xxx_design.md` file has a matching `src/xxx/` directory:

| Design | Matching implementation |
|---|---|
| `firmware_design.md` | `src/firmware/` |
| `android_design.md` | `src/android/` |
| `business_logic_design.md` | `src/business_logic/` |
| `database_design.md` | `src/database/` |
| `api_rest_design.md` | `src/api_rest/` |
| `web_design.md` | `src/web/` |

Each specification deliberately contains the three sections required by the reviewer:
`Component Design`, `Design Clarifications`, and `General Rules`.
The original runnable project layout is kept in the repository because Arduino, Android/Gradle and the Plesk deployment use their native folder structures; `src/` is a synchronized reviewer-facing mirror, not an alternative implementation.
