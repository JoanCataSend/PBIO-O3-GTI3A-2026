# API REST del Sprint 0

URL desplegada:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

La API es un adaptador HTTP/JSON. La lógica de negocio se encuentra en `server/Logica.php`.

## GET health

```text
GET /biometria/api.php?accion=health
```

Respuesta `200`:

```json
{
  "ok": true,
  "database": "jcatsen_pbio"
}
```

## GET medidas

```text
GET /biometria/api.php
```

Filtros opcionales:

```text
dispositivoId
tipoMedidaId
desde
hasta
```

Respuesta `200`: lista de `MedidaVista` ordenada de más reciente a más antigua.

## GET última medida

```text
GET /biometria/api.php?accion=ultima&dispositivoId=1&tipoMedidaId=14
```

Respuesta `200`: una `MedidaVista`.

## GET dispositivos

```text
GET /biometria/api.php?accion=dispositivos
```

## GET tipos de medida

```text
GET /biometria/api.php?accion=tipos
```

## POST insertar medida

```text
POST /biometria/api.php
Content-Type: application/json
```

Cuerpo `MedidaEntrada`:

```json
{
  "uuid": "EPSG-GTI-PROY-3A",
  "tipoMedidaId": 14,
  "valor": 123,
  "contador": 16,
  "rssi": -58
}
```

El servidor genera `medidaId` y `fechaHora`.

Respuesta `201`: `MedidaVista` creada.

## Errores HTTP

| Código | Significado |
|---:|---|
| 400 | Entrada no válida / JSON incorrecto |
| 404 | Dispositivo, tipo o medida no existente |
| 405 | Método HTTP no permitido |
| 500 | Error interno no previsto |

## Tipos lógicos

```text
MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)

MedidaVista = (
    medidaId:N,
    dispositivoId:N,
    uuid:Texto,
    dispositivo:Texto,
    tipoMedidaId:N,
    tipoMedida:Texto,
    unidad:Texto,
    valor:Z,
    contador:N,
    rssi:Z,
    fechaHora:Texto
)
```

Diseño formal completo: `docs/disenos/05-api-rest.md`.
