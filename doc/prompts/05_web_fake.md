# Prompt 5 - Lógica fake del navegador

```text
Implementa únicamente `src/web/js/LogicaFake.js`. No escribas HTML/CSS ni manipules DOM.

OBJETIVO
Ser la única capa del navegador que conoce `fetch` y la API REST.

API
Usa exclusivamente `const API = "api.php"` (ruta relativa, sin dominio absoluto).

CONTRATOS
url: Text,opciones: PeticionHTTP --> pedir() --> datos: Json
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]
health() --> estado: EstadoBD

REGLAS
- `pedir()` debe comprobar `response.ok` y convertir errores HTTP en Error con mensaje del JSON si existe.
- `listarMedidas()` solo añade parámetros no vacíos mediante URLSearchParams.
- Acciones: dispositivos, tipos y health según el diseño REST.
- No guardar estado de UI.

COMENTARIOS
Cabecera completa con Joan Catala Sendra. Cada función con `--------------------`, firma lógica oficial y descripción.

TEST
El test web debe comprobar, al menos, que esta capa usa `api.php` relativo y no contiene URLs http/https codificadas.

Entrega el archivo completo, sin frameworks ni dependencias externas.
```
