# Correspondencia entre ingeniería inversa, diseño e implementación

## Firmware

| Responsabilidad lógica | Implementación |
|---|---|
| Proporcionar medidas | `Medidor` |
| Codificar ID/contador/valor y publicar | `Publicador` |
| Gestionar Bluefruit/iBeacon | `EmisoraBLE` |
| Coordinar el ciclo | `NodoO3.ino` |

En Sprint 0 `Medidor` es deliberadamente **fake**: devuelve `123 ppb` y `-12 °C`.

## Android

| Responsabilidad lógica | Implementación |
|---|---|
| Coordinar escaneo/UI | `MainActivity` |
| Parsear iBeacon | `TramaIBeacon` |
| Conversiones de bytes | `Utilidades` |
| Representar entrada al negocio | `MedidaEntrada` |
| Exponer operación de negocio al cliente | `LogicaFake` |
| Transporte HTTP | `PeticionarioREST` |

## Backend

| Responsabilidad lógica | Implementación |
|---|---|
| Adaptar HTTP/JSON | `web/api.php` |
| Validar y ejecutar negocio | `server/Logica.php` |
| Persistencia | MariaDB |

## Navegador

| Responsabilidad lógica | Implementación |
|---|---|
| Invocar operaciones lógicas | `web/js/LogicaFake.js` |
| Controlar UI | `web/js/app.js` |

## Elementos del código original no reintroducidos

El código proporcionado históricamente incluía auxiliares como `LED`, `PuertoSerie`, `ServicioEnEmisora` y `Caracteristica`.

No se añaden como código muerto porque la arquitectura ejecutada en Sprint 0:

- utiliza `Serial` directamente;
- publica iBeacon mediante advertising no conectable;
- no necesita un servicio GATT ni características conectables;
- no necesita encapsular un LED para demostrar el criterio de aceptación.

La ausencia de esas clases está justificada por la implementación realmente utilizada.

## Coherencia

Los diseños formales de `docs/disenos/` se han extraído de la implementación actual y omiten detalles de lenguaje, punteros, callbacks y API específicas cuando no forman parte del diseño lógico.
