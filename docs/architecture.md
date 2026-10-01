# Arquitectura del Sprint 0

> Este documento describe **la rama Sprint 0 (`main`/`master`)**. En esta versión las medidas no proceden del sensor físico: `Medidor` devuelve valores ficticios reproducibles.

## Flujo completo

```text
Medidor (firmware)
O3 = 123 ppb
Temperatura = -12 °C
        ↓
Publicador + EmisoraBLE
        ↓ iBeacon
Android / MainActivity
        ↓
LogicaFake.java
        ↓ HTTPS + JSON
web/api.php
        ↓
server/Logica.php
        ↓
MariaDB: jcatsen_pbio
        ↑
web/api.php
        ↑
LogicaFake.js
        ↑
Página web
```

## Responsabilidades

### Firmware

- `Medidor.h`: proporciona valores ficticios constantes para la prueba reproducible.
- `Publicador.h`: asigna ID de medida y codifica `Major` y `Minor`.
- `EmisoraBLE.h`: configura Bluefruit y publica el iBeacon.
- `NodoO3.ino`: coordina el ciclo de publicación.

### Android

- `MainActivity`: escanea BLE, valida nombre/UUID, decodifica el protocolo y actualiza la UI.
- `TramaIBeacon`: extrae UUID, Major, Minor y TxPower.
- `Utilidades`: conversiones de bytes.
- `MedidaEntrada`: datos enviados al servidor.
- `LogicaFake`: expone la operación lógica `insertarMedida` al cliente.
- `PeticionarioREST`: implementa el transporte HTTPS/JSON.

### Backend

- `web/api.php`: conoce HTTP y convierte cada petición en una llamada a la lógica de negocio.
- `server/Logica.php`: valida datos, realiza operaciones de negocio y accede a MariaDB.
- `server/SDBaseDatos.php`: configuración privada local del servidor; no se versiona.

### Base de datos

- `Dispositivo`: identifica cada nodo.
- `TipoMedida`: catálogo de magnitudes.
- `Medida`: histórico de valores recibidos.

### Web

- `web/js/LogicaFake.js`: adapta las operaciones lógicas a peticiones `fetch`.
- `web/js/app.js`: controla filtros, tarjetas, tabla, gráfica y refresco automático.
- `web/index.html` + `web/css/styles.css`: interfaz responsive.

## Separación de responsabilidades

```text
UI Android ──> LogicaFake.java ──> PeticionarioREST
                                   │
                                   ▼
                              API REST
                                   │
                                   ▼
                            Logica.php
                                   │
                                   ▼
                                MariaDB

UI Web ──────> LogicaFake.js ─────┘
```

La UI no contiene SQL. La lógica de negocio no conoce vistas ni componentes Android. `Logica.php` no depende del navegador ni de la actividad Android.

## Protocolo de medida

```text
Major = [ID de medida: 8 bits][contador: 8 bits]
Minor = valor de la medida en 16 bits
```

IDs:

| Medida | ID |
|---|---:|
| CO2 | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O3 | 14 |

En Sprint 0 se publican:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

## Versión con sensor real

La adquisición física del ULPSM-O3 no pertenece al Sprint 0 reproducible. Se conserva en la rama `sensor-real-final`. La documentación específica de esa evolución está en `docs/sensor-real-final/`.
