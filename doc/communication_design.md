# communication_design.md

## Diseño del Componente

**Componente:** `communication`
**Implementación:** `src/communication/`
**Lenguaje:** PHP 8.

Este componente contiene exclusivamente el punto de entrada HTTP/JSON. Interpreta una petición externa, invoca operaciones de `business_logic` y traduce el resultado al protocolo de comunicación. No contiene SQL ni reglas de dominio.

### Tipos de comunicación

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

Los tipos anteriores pertenecen solo a esta capa de comunicación y no aparecen en `business_logic_design.md`.

### Puntos de entrada REST

```text
POST api.php
    cuerpo lógico: MedidaEntrada
    invoca: insertarMedida()
    respuesta: 201 + MedidaVista

GET api.php?accion=medidas
    invoca: listarMedidas()
    respuesta: 200 + [MedidaVista]

GET api.php?accion=ultima&dispositivoId=<Text>&tipoMedidaId=<N>
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

### Operación propia del adaptador

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

### Traducción de resultados del adaptador

```text
entrada inválida           -> 400
entidad de dominio ausente -> 404
método no soportado        -> 405
fallo no previsto          -> 500 con mensaje genérico
```

## Aclaraciones del Diseño

- El parámetro externo `dispositivoId` es `Text` porque la base simplificada utiliza directamente el identificador textual de 16 caracteres del nodo.
- `tipoMedidaId` se convierte a `N` antes de invocar la lógica de negocio.
- `api.php` no abre la base directamente y no contiene sentencias SQL.
- La respuesta de error interno no expone credenciales ni detalles de persistencia.
- En el repositorio la lógica está en `src/business_logic/Logica.php`; el empaquetado de despliegue puede copiarla a una ubicación privada del servidor sin duplicarla en Git.

## Reglas Generales

- **Lenguaje de Programación:** PHP 8.
- **Encabezados de Funciones/Métodos:** toda función propia debe incluir diseño lógico entre `--------------------` y breve descripción.
- **Legibilidad del Código:** mantener el adaptador reducido a decodificación, despacho y traducción de errores.
- **Pruebas Automatizadas:** `src/communication/tests/ApiIntegracionTest.php` verifica el contrato y permite una integración HTTP real opcional.
