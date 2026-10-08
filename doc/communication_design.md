# communication_design.md

## DiseÃ±o del Componente

**Componente:** `communication`
**ImplementaciÃ³n:** `src/communication/`
**Lenguaje:** PHP 8.

Este componente contiene exclusivamente el punto de entrada HTTP/JSON. Interpreta una peticiÃ³n externa, invoca operaciones de `business_logic` y traduce el resultado al protocolo de comunicaciÃ³n. No contiene SQL ni reglas de dominio.

### Tipos de comunicaciÃ³n

```text
PeticionHTTP = (
    metodo: Text,
    accion: Text,
    parametros: Text,
    cuerpo: Text
)

RespuestaHTTP = (
    codigo: N,
    cuerpo: Text
)
```

Los tipos anteriores pertenecen solo a esta capa de comunicaciÃ³n y no aparecen en `business_logic_design.md`.

### Puntos de entrada REST

```text
POST api.php
    cuerpo lÃ³gico: MedidaEntrada
    invoca: insertarMedida()
    respuesta: 201 + MedidaVista

GET api.php?accion=medidas
    invoca: listarMedidas()
    respuesta: 200 + [MedidaVista]

GET api.php?accion=ultima&dispositivoId=<N>&tipoMedidaId=<N>
    invoca: buscarUltimaMedida()
    respuesta: 200 + MedidaVista

GET api.php?accion=dispositivos
    invoca: listarDispositivos()
    respuesta: 200 + [Dispositivo]

GET api.php?accion=tipos
    invoca: listarTiposMedida()
    respuesta: 200 + [TipoMedida]

GET api.php?accion=health
    invoca: probarConexion()
    respuesta: 200 + EstadoBD
```

### OperaciÃ³n propia del adaptador

```text
codigo: N, datos: Text --> responder()
```

### Flujo de dependencias

```text
communication
    |
    v
business_logic
    |
    v
database
```

`communication` puede invocar `business_logic`; la dependencia inversa no existe.

### TraducciÃ³n de resultados del adaptador

```text
entrada invÃ¡lida         -> 400
entidad de dominio ausente -> 404
mÃ©todo no soportado      -> 405
fallo no previsto        -> 500 con mensaje genÃ©rico
```

## Aclaraciones del DiseÃ±o

- Los parÃ¡metros externos `dispositivoId` y `tipoMedidaId` se convierten a los tipos lÃ³gicos `N` antes de invocar la lÃ³gica de negocio.
- `api.php` no abre la base directamente y no contiene sentencias SQL.
- La respuesta de error interno no expone credenciales ni detalles de persistencia.
- En el repositorio la lÃ³gica estÃ¡ en `src/business_logic/Logica.php`; el empaquetado de despliegue puede copiarla a una ubicaciÃ³n privada del servidor sin duplicarla en Git.

## Reglas Generales

- **Lenguaje de ProgramaciÃ³n:** PHP 8.
- **Encabezados de Funciones/MÃ©todos:** toda funciÃ³n propia debe incluir diseÃ±o lÃ³gico entre `--------------------` y breve descripciÃ³n.
- **Legibilidad del CÃ³digo:** limitar esta capa a decodificaciÃ³n, despacho, serializaciÃ³n y traducciÃ³n de errores de comunicaciÃ³n.
- **Pruebas Automatizadas:** `src/communication/tests/ApiIntegracionTest.php` verifica mÃ©todos, acciones y cÃ³digos de respuesta del servidor desplegado.
