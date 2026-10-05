# web_design.md

## Component Design (Diseño del Componente)

**Componente:** `web`
**Implementación canónica para revisión:** `src/web/`
**Código operativo equivalente:** `web/index.html`, `web/css/` y `web/js/`

La interfaz web consulta la API, presenta estado de conexión, filtros, últimos valores, histórico y una gráfica. La UI no accede directamente a MariaDB.


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


### Arquitectura

```text
index.html + styles.css
        |
        v
      app.js
        |
        v
  LogicaFake.js
        |
        v
     api.php
```

### Contratos de acceso a datos

```text
url:Texto, opciones:PeticionHTTP --> pedir() --> JSON | Error
filtros:FiltrosMedida --> listarMedidas() --> [MedidaVista] | Error
listarDispositivos() --> [Dispositivo] | Error
listarTiposMedida() --> [TipoMedida] | Error
health() --> EstadoBD | Error
```

### Contratos de UI

```text
iniciar() -->
cargarCatalogos() -->
actualizar() -->
valor:Texto --> fechaSql() --> Texto
medidas:[MedidaVista] --> pintarResumen() -->
medidas:[MedidaVista] --> pintarTabla() -->
medidas:[MedidaVista] --> pintarGrafica() -->
valor:Texto --> escapar() --> Texto
error:Error --> mostrarError() -->
```

### Flujo principal

```text
iniciar:
    registrar botón Actualizar
    health()
    cargarCatalogos()
    actualizar()
    programar actualizar() cada 5 s

actualizar:
    leer filtros UI
    listarMedidas(filtros)
    pintarResumen
    pintarTabla
    pintarGrafica
    actualizar hora/estado de conexión
```

### Descripción textual de la GUI

La web es una única pantalla responsive. La cabecera muestra **Monitor ambiental · GTI Joan** y un estado de conexión accesible. A la izquierda/en la parte superior en móvil se sitúan filtros por dispositivo, tipo, fecha desde y fecha hasta, junto con el botón **Actualizar**. El área principal contiene tres tarjetas de resumen (O3, Temperatura y último RSSI), una gráfica temporal en `canvas` y una tabla histórica con fecha/hora, dispositivo, tipo, valor y RSSI. Si el filtro no devuelve filas se presenta un mensaje de “sin datos”.

### Archivos del componente

```text
index.html
css/styles.css
js/LogicaFake.js
js/app.js
```

### UX y seguridad

- Diseño responsive para móvil.
- Controles táctiles de al menos 44 px y `safe-area` para notch.
- Tabla con desplazamiento horizontal.
- Estado de conexión visible mediante `role="status"`/`aria-live`.
- O3 y temperatura priorizados en el resumen.
- Datos del histórico escapados mediante `escapar()` antes de insertarse como HTML.
- Canvas ajustado al tamaño CSS real y `devicePixelRatio`.

## Design Clarifications (Aclaraciones del Diseño)

- `LogicaFake.js` encapsula `fetch`; el nombre se conserva por continuidad con la asignatura, pero consulta la API real.
- El navegador usa `api.php` como ruta relativa para que el mismo frontend funcione en el despliegue `/biometria/`.
- Si hay menos de dos puntos de un tipo, la gráfica muestra un mensaje en lugar de dibujar una serie engañosa.
- Las fechas `datetime-local` se transforman al formato SQL esperado por la API.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** JavaScript ES2020+, HTML5 y CSS3 sin frameworks externos.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** cada función JavaScript incluye diseño lógico dentro de un bloque delimitado por `--------------------`.
- **Code Readability / Legibilidad del Código:** `LogicaFake.js` concentra acceso REST; `app.js` concentra UI; HTML y CSS permanecen declarativos y separados.
- **Automated Testing / Pruebas Automatizadas:** `src/web/tests/web_unit_test.js` carga el `app.js` real en un contexto aislado y prueba `fechaSql()` y `escapar()`; la API utilizada por la web se cubre además mediante el test de integración REST.
- **Source correspondence / Correspondencia:** `src/web/` contiene los mismos HTML/CSS/JS que el frontend operativo, sin incluir `api.php` porque este pertenece al componente `api_rest`.
