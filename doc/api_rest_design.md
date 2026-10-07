# api_rest_design.md

## Diseño del Componente

**Componente:** `api_rest`  
**Implementación:** `src/api_rest/`  
**Lenguaje:** PHP 8.0 o superior.

Este componente es un adaptador. Interpreta HTTP/JSON, llama a funciones ya diseñadas en `business_logic` y traduce resultados/excepciones a códigos HTTP. No contiene reglas de negocio ni SQL.

### Tipos lógicos

```text
Json = Text

PeticionHTTP = (
    metodo: Text,
    accion: Text,
    parametros: Json,
    cuerpo: Json
)

RespuestaHTTP = (
    codigo: N,
    cuerpo: Json
)
```

### Rutas

```text
POST api.php
    cuerpo: MedidaEntrada
    -> insertarMedida()
    -> 201 MedidaVista

GET api.php?accion=medidas
    filtros opcionales
    -> listarMedidas()
    -> 200 [MedidaVista]

GET api.php?accion=ultima&dispositivoId=<N>&tipoMedidaId=<N>
    -> buscarUltimaMedida()
    -> 200 MedidaVista

GET api.php?accion=dispositivos
    -> listarDispositivos()
    -> 200 [Dispositivo]

GET api.php?accion=tipos
    -> listarTiposMedida()
    -> 200 [TipoMedida]

GET api.php?accion=health
    -> probarConexion()
    -> 200 EstadoBD
```

### Operación propia del adaptador

```text
codigo: N, datos: Json --> responder() -->
```

### Algoritmo de despacho

```text
si metodo = POST:
    decodificar cuerpo JSON
    insertarMedida(datos)
    responder(201, medida)

si metodo != GET:
    responder(405, error)

según accion:
    health        -> probarConexion()
    dispositivos  -> listarDispositivos()
    tipos          -> listarTiposMedida()
    ultima         -> buscarUltimaMedida()
    medidas        -> listarMedidas()
    otra           -> Error de entrada
```

### Traducción de errores

```text
InvalidArgumentException -> 400
DomainException          -> 404
método no soportado      -> 405
Throwable no previsto    -> 500 con mensaje genérico
```

## Aclaraciones del Diseño

- Los parámetros HTTP usan `dispositivoId` y `tipoMedidaId`; en el diseño lógico se representan como `dispositivo_id` y `tipo_medida_id` para respetar la convención de variables de la notación oficial.
- La API no abre MariaDB directamente: siempre pasa por `business_logic`.
- La respuesta `500` no expone mensajes internos ni credenciales.
- En el repositorio `Logica.php` está en `src/business_logic/`; en Plesk se despliega como `server/Logica.php`. `api.php` admite ambos emplazamientos sin duplicar código.
- La web usa `api.php` relativo y Android usa HTTPS absoluto.

## Reglas Generales

- **Lenguaje de Programación:** PHP 8.0 o superior; no usar tipos o sintaxis introducidos después de PHP 8.0.
- **Encabezados de Funciones/Métodos:** toda función propia debe incluir diseño lógico entre `--------------------` y breve descripción.
- **Legibilidad del Código:** el adaptador debe limitarse a parseo, despacho y códigos HTTP; cualquier regla de dominio pertenece a `business_logic`.
- **Pruebas Automatizadas:** `src/api_rest/tests/ApiIntegracionTest.php` comprueba lecturas y códigos 400/404/405 sin modificar la BBDD por defecto. Admite `PBIO_API_URL` para cambiar el servidor y `PBIO_API_WRITE_TEST=1` para habilitar de forma explícita un POST válido que inserta una medida de prueba.
