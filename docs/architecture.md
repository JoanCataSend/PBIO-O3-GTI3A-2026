# Arquitectura final

```text
ULPSM-O3
   ↓
SparkFun nRF52840
   ↓ BLE / iBeacon
Android
   ↓ HTTP / JSON
api.php
   ↓
Logica.php
   ↓
MariaDB (jcatsen_pbio)
   ↑
api.php
   ↑
LogicaFake.js
   ↑
Página web
```

La solución mantiene separadas las responsabilidades importantes sin añadir
infraestructura innecesaria para la práctica.

## Firmware

- mide O₃ y temperatura;
- publica iBeacon;
- `Major = (ID << 8) | contador`;
- `Minor = valor`.

## Android

- busca `GTI Joan`;
- valida `EPSG-GTI-PROY-3A`;
- interpreta `Major` y `Minor`;
- muestra las medidas;
- envía una sola vez cada medida nueva a `api.php`.

## Backend

`web/api.php` adapta HTTP/JSON.

`server/Logica.php` contiene la validación y las consultas SQL.

`server/SDBaseDatos.php` contiene únicamente la configuración de MariaDB
del servidor Plesk y está ignorado por Git.

## Web

La UX llama exclusivamente a `LogicaFake.js`.

`LogicaFake.js` realiza las peticiones a `api.php`.
