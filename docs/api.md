# API sencilla

Endpoint:

```text
http://pbio.jcatsen.upv.edu.es/api.php
```

## Comprobar conexión

```text
GET api.php?accion=health
```

## Listar medidas

```text
GET api.php
```

Filtros opcionales:

```text
dispositivoId
tipoMedidaId
desde
hasta
```

## Listar dispositivos

```text
GET api.php?accion=dispositivos
```

## Listar tipos

```text
GET api.php?accion=tipos
```

## Insertar medida

```text
POST api.php
Content-Type: application/json
```

Ejemplo:

```json
{
  "uuid": "EPSG-GTI-PROY-3A",
  "tipoMedidaId": 14,
  "valor": 291,
  "contador": 16,
  "rssi": -58
}
```

El servidor genera `medidaId` y `fechaHora`.
