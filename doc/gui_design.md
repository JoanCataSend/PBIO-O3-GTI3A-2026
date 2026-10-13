# gui_design.md

## Diseño del Componente

**Componente:** `gui`
**Implementación:** `src/gui/`
**Lenguajes:** HTML5, CSS3 y JavaScript.

Este componente implementa exclusivamente la interfaz gráfica web. No realiza comunicación remota directamente: todas las operaciones de dominio se solicitan a `frontend_business_logic` mediante `LogicaFake`.

### Dependencia permitida

```text
gui
 |
 v
frontend_business_logic
```

No existe `fetch`, URL del servidor ni código de protocolo dentro de `src/gui/js/app.js`.

### Operaciones de la GUI

```text
iniciar()

cargarCatalogos()

actualizar()

valor: Text --> fechaSql() --> fecha: Text

medidas: [MedidaVista] --> pintarResumen()

medidas: [MedidaVista] --> pintarTabla()

medidas: [MedidaVista] --> pintarGrafica()

valor: Text --> escapar() --> texto_seguro: Text

mensaje: Text --> mostrarError()
```

### Operaciones de dominio consumidas

```text
probarConexion() --> estado: EstadoBD

filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]

listarDispositivos() --> dispositivos: [Dispositivo]

listarTiposMedida() --> tipos: [TipoMedida]
```

Estas firmas pertenecen a `frontend_business_logic_design.md`; la GUI solo las consume.

### Flujo de inicio

```text
registrar eventos
probarConexion()
si falla -> mostrarError() y terminar
cargarCatalogos()
actualizar()
programar actualizar() cada 5 s
```

### Descripción textual de la GUI

- Cabecera con nombre del monitor y estado de conexión.
- Panel de filtros por dispositivo, tipo y rango temporal.
- Tarjetas de última medida de O3 y temperatura.
- Gráfica temporal del tipo seleccionado.
- Tabla histórica con fecha, dispositivo, tipo y valor/unidad.
- Botón de actualización manual y refresco automático cada 5 segundos.

### UX, accesibilidad y seguridad

- Diseño responsive para móvil y escritorio.
- Controles táctiles de al menos 44 px e inputs de 16 px en móvil.
- Compatibilidad con `safe-area`.
- Estado de conexión con `role="status"` y `aria-live`.
- Tabla desplazable horizontalmente en pantallas estrechas.
- Valores escapados antes de insertarlos mediante `innerHTML`.
- Sin frameworks ni dependencias externas.

## Aclaraciones del Diseño

- `app.js` no contiene `fetch` ni construye rutas de red.
- `LogicaFake` pertenece a `frontend_business_logic`, no al componente GUI.
- Si no se selecciona un tipo, la gráfica prioriza O3; las tarjetas muestran el valor más reciente de cada tipo dentro del conjunto recibido.
- RSSI sigue visible en Android como dato de recepción BLE, pero deja de aparecer en la web porque ya no se persiste.
- La comprobación visual presencial complementa, pero no sustituye, las pruebas automatizadas.

## Reglas Generales

- **Lenguaje de Programación:** JavaScript sin frameworks, HTML5 y CSS3.
- **Encabezados de Funciones/Métodos:** cada función JavaScript propia debe incluir diseño lógico entre `--------------------` y breve descripción.
- **Legibilidad del Código:** separar manipulación del DOM, presentación y llamadas a la interfaz de `frontend_business_logic`.
- **Pruebas Automatizadas:** `src/gui/tests/gui_unit_test.js` verifica transformaciones puras, escape, resumen y ausencia de comunicación directa.
