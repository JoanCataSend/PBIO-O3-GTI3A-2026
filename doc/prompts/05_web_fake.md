# Prompt 5 - Frontend business logic web

```text
Implementa únicamente `src/frontend_business_logic/web/LogicaFake.js` según `doc/frontend_business_logic_design.md`.

La GUI está en `src/gui/` y NO debe contener fetch. Este archivo es la única capa del navegador que conoce la comunicación.

INTERFAZ LÓGICA - nombres y firmas idénticos al backend
probarConexion() --> estado: EstadoBD
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]

IMPLEMENTACIÓN INTERNA
- `const API = "api.php"`, sin host absoluto.
- `pedir()` encapsula fetch, parseo de respuesta y errores.
- `listarMedidas()` añade únicamente filtros no vacíos.
- acciones: health, dispositivos y tipos.
- no manipular DOM ni mantener estado visual.

Cada función propia debe incluir bloque `--------------------`, diseño lógico y descripción.

Genera/actualiza `src/frontend_business_logic/tests/web_proxy_test.js` para comprobar API relativa, ausencia de URLs absolutas, existencia de las cuatro operaciones públicas y que fetch solo aparece en este componente.
```
