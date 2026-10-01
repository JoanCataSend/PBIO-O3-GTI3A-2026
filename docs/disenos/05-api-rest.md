# 05 · Diseño del API REST y protocolo HTTP

## Principio

La API REST es un adaptador de protocolo. **No implementa reglas de negocio**: traduce mensajes HTTP a llamadas de `Logica.php` y traduce resultados/excepciones a respuestas HTTP.

## Mensajes cliente -> servidor

### Insertar medida

```text
POST /biometria/api.php
Content-Type application/json

<uuid:Texto>
<tipoMedidaId:N>
<valor:Z>
<contador:N>
<rssi:Z>
```

Representación JSON real:

```json
{
  "uuid": "EPSG-GTI-PROY-3A",
  "tipoMedidaId": 14,
  "valor": 123,
  "contador": 16,
  "rssi": -58
}
```

### Listar medidas

```text
GET /biometria/api.php
[dispositivoId:N]
[tipoMedidaId:N]
[desde:Texto]
[hasta:Texto]
```

### Última medida

```text
GET /biometria/api.php?accion=ultima
<dispositivoId:N>
<tipoMedidaId:N>
```

### Catálogos

```text
GET /biometria/api.php?accion=dispositivos
GET /biometria/api.php?accion=tipos
GET /biometria/api.php?accion=health
```

## Mensajes servidor -> cliente

```text
201 <MedidaVista>
200 <MedidaVista>
200 <[MedidaVista]>
200 <[Dispositivo]>
200 <[TipoMedida]>
200 <EstadoBD>
```

Errores:

```text
ERROR 400 <mensaje:Texto>
ERROR 404 <mensaje:Texto>
ERROR 405 <mensaje:Texto>
ERROR 500 <mensaje:Texto>
```

Significado:

```text
ERROR 400 -> entrada inválida
ERROR 404 -> recurso lógico no encontrado
ERROR 405 -> método HTTP no soportado
ERROR 500 -> fallo interno no previsto
```

## Interacción de inserción

```text
Android                api.php                 Logica.php              MariaDB
   |                       |                        |                      |
   | POST MedidaEntrada    |                        |                      |
   |---------------------->|                        |                      |
   |                       | insertarMedida(datos)  |                      |
   |                       |----------------------->|                      |
   |                       |                        | validar              |
   |                       |                        | consultar/insertar    |
   |                       |                        |--------------------->|
   |                       |                        |<---------------------|
   |                       |<-----------------------| MedidaVista           |
   |<----------------------| 201 MedidaVista        |                      |
```

## Interacción de consulta web

```text
Navegador              api.php                 Logica.php              MariaDB
   |                       |                        |                      |
   | GET medidas           |                        |                      |
   |---------------------->| listarMedidas()        |                      |
   |                       |----------------------->|--------------------->|
   |                       |                        |<---------------------|
   |                       |<-----------------------| [MedidaVista]         |
   |<----------------------| 200 [MedidaVista]      |                      |
```
