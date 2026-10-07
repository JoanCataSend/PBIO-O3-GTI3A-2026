# web_design.md

## Diseño del Componente

**Componente:** `web`  
**Implementación:** `src/web/`  
**Lenguajes:** HTML5, CSS3 y JavaScript.

La interfaz web separa presentación de acceso remoto. `app.js` controla DOM/UX y `LogicaFake.js` es la única capa que conoce `fetch` y las rutas REST.

### Tipos lógicos

```text
Json = Text

PeticionHTTP = (
    metodo: Text,
    cabeceras: Text,
    cuerpo: Json
)

Dispositivo = (
    dispositivo_id: N,
    uuid: Text,
    nombre: Text
)

TipoMedida = (
    tipo_medida_id: N,
    nombre: Text,
    unidad: Text
)

MedidaVista = (
    medida_id: N,
    dispositivo_id: N,
    uuid: Text,
    dispositivo: Text,
    tipo_medida_id: N,
    tipo_medida: Text,
    unidad: Text,
    valor: Z,
    contador: N,
    rssi: Z,
    fecha_hora: Text
)

FiltrosMedida = (
    dispositivo_id: N,
    tipo_medida_id: N,
    desde: Text,
    hasta: Text
)

Los cuatro campos son filtros de dominio. En la petición web pueden omitirse; cuando están presentes, identificadores son `N` y fechas son `Text`.

EstadoBD = (ok: B, database: Text)
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

### `LogicaFake.js`

```text
url: Text, opciones: PeticionHTTP --> pedir() --> datos: Json
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]
health() --> estado: EstadoBD
```

`api.php` es una ruta relativa; no se codifica un host en el navegador.

### `app.js`

```text
iniciar() -->
cargarCatalogos() -->
actualizar() -->
valor: Text --> fechaSql() --> fecha: Text
medidas: [MedidaVista] --> pintarResumen() -->
medidas: [MedidaVista] --> pintarTabla() -->
medidas: [MedidaVista] --> pintarGrafica() -->
valor: Text --> escapar() --> texto_seguro: Text
mensaje: Text --> mostrarError() -->
```

### Flujo de inicio

```text
registrar eventos
health()
si falla -> mostrarError() y terminar
cargarCatalogos()
actualizar()
programar actualizar() cada 5 s
```

### Flujo de actualización

```text
leer filtros de la UI
convertir fechas al formato esperado
listarMedidas(filtros)
pintarResumen(medidas)
pintarTabla(medidas)
pintarGrafica(medidas)
actualizar estado y hora de refresco
```

### Descripción textual de la GUI

- Cabecera con nombre del monitor y estado de conexión.
- Panel de filtros por dispositivo, tipo y rango temporal.
- Tarjetas de última medida de O3, temperatura y RSSI.
- Gráfica temporal del tipo seleccionado.
- Tabla histórica con fecha, dispositivo, tipo, valor/unidad y RSSI.
- Botón de actualización manual más refresco automático cada 5 segundos.

### UX, accesibilidad y seguridad

- Diseño responsive para móvil y escritorio.
- Controles táctiles de al menos 44 px e inputs de 16 px en móvil.
- `safe-area` para dispositivos con notch.
- Estado de conexión con `role="status"` y `aria-live`.
- Tabla con scroll horizontal en pantallas estrechas.
- Valores escapados antes de insertarlos mediante `innerHTML`.
- Sin frameworks ni dependencias externas.

## Aclaraciones del Diseño

- La notación lógica usa variables en minúsculas con guion bajo; los objetos JSON recibidos conservan las claves del API (`tipoMedidaId`, `fechaHora`, etc.). La equivalencia está definida uno-a-uno.
- La lógica fake del navegador es `LogicaFake.js`; no contiene decisiones de presentación.
- `app.js` no conoce SQL ni credenciales y no realiza `fetch` directamente.
- Si no se selecciona un tipo, la gráfica prioriza O3; las tarjetas muestran la medida más reciente disponible de cada tipo en el conjunto filtrado.
- Las evidencias visuales sirven como apoyo, pero no sustituyen el test presencial extremo a extremo.

## Reglas Generales

- **Lenguaje de Programación:** JavaScript sin frameworks, HTML5 y CSS3.
- **Encabezados de Funciones/Métodos:** cada función JavaScript debe incluir su diseño lógico entre `--------------------` y breve descripción.
- **Legibilidad del Código:** separar acceso REST, control de UI y presentación; mantener nombres semánticos y no duplicar rutas.
- **Pruebas Automatizadas:** `src/web/tests/web_unit_test.js` verifica conversión temporal, escape HTML y que la lógica fake use API relativa.
