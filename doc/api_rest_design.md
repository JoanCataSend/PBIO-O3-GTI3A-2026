# api_rest_design.md

## Component Design (Diseño del Componente)

**Componente:** `api_rest`
**Implementación canónica para revisión:** `src/api_rest/`
**Código operativo equivalente:** `web/api.php`

La API REST es un adaptador HTTP/JSON. Lee método, query string o body, invoca operaciones de `business_logic` y traduce resultados/excepciones a códigos HTTP. No contiene SQL.


### Tipos lógicos comunes

```text
N      número natural
Z      número entero
R      número real
VoF    booleano
Texto  cadena de caracteres
[T]    colección de T
[T]_n  array de T de tamaño fijo n
JSON   Texto con estructura JSON
```

Tipos del dominio:

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


### Endpoint

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

### Mensajes cliente -> servidor

```text
POST /biometria/api.php
<MedidaEntrada JSON>
    --> 201 <MedidaVista JSON>

GET /biometria/api.php?[dispositivoId][tipoMedidaId][desde][hasta]
    --> 200 <[MedidaVista]>

GET /biometria/api.php?accion=ultima&dispositivoId=N&tipoMedidaId=N
    --> 200 <MedidaVista>

GET /biometria/api.php?accion=dispositivos
    --> 200 <[Dispositivo]>

GET /biometria/api.php?accion=tipos
    --> 200 <[TipoMedida]>

GET /biometria/api.php?accion=health
    --> 200 <EstadoBD>
```

### Errores

```text
InvalidArgumentException -> 400
DomainException -> 404
método distinto de GET/POST -> 405
Throwable no previsto -> 500 con mensaje genérico
```

### Función propia del adaptador

```text
codigo:N, datos:JSON --> responder() -->
```

`responder()` fija el código HTTP, serializa JSON UTF-8 y finaliza la petición. El resto del archivo es enrutamiento de nivel superior y delega la lógica a `Logica.php`.

## Design Clarifications (Aclaraciones del Diseño)

- El API soporta dos ubicaciones de despliegue para `Logica.php`: `server/` dentro de `/biometria` en Plesk y `../server/` en el árbol del repositorio.
- Las credenciales de base de datos nunca aparecen en `api.php` ni en el repositorio.
- El `POST` exige un objeto JSON; cuerpos inválidos se rechazan como error de entrada.
- La API devuelve un mensaje genérico en errores 500 para no exponer detalles internos.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** PHP 8 para HTTP/JSON.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** toda función declarada incluye diseño lógico dentro de un bloque delimitado por `--------------------`.
- **Code Readability / Legibilidad del Código:** el enrutamiento se expresa por casos simples y delega la lógica de negocio; no se duplica SQL ni validación de dominio.
- **Automated Testing / Pruebas Automatizadas:** `src/api_rest/tests/ApiIntegracionTest.php` ejecuta comprobaciones HTTPS de `health`, base de datos declarada, dispositivo, tipos O3/temperatura y colección de medidas.
- **Source correspondence / Correspondencia:** `src/api_rest/api.php` coincide con el endpoint operativo `web/api.php`.
