# gui_design.md

## DiseÃ±o del Componente

**Componente:** `gui`
**ImplementaciÃ³n:** `src/gui/`
**Lenguajes:** HTML5, CSS3 y JavaScript.

Este componente implementa exclusivamente la interfaz grÃ¡fica web. No realiza comunicaciÃ³n remota directamente: todas las operaciones de dominio se solicitan a `frontend_business_logic` mediante `LogicaFake`.

### Dependencia permitida

```text
gui
 |
 v
frontend_business_logic
```

No existe `fetch`, URL del servidor ni cÃ³digo de protocolo dentro de `src/gui/js/app.js`.

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

### DescripciÃ³n textual de la GUI

- Cabecera con nombre del monitor y estado de conexiÃ³n.
- Panel de filtros por dispositivo, tipo y rango temporal.
- Tarjetas de Ãºltima medida de O3, temperatura y RSSI.
- GrÃ¡fica temporal del tipo seleccionado.
- Tabla histÃ³rica con fecha, dispositivo, tipo, valor/unidad y RSSI.
- BotÃ³n de actualizaciÃ³n manual y refresco automÃ¡tico cada 5 segundos.

### UX, accesibilidad y seguridad

- DiseÃ±o responsive para mÃ³vil y escritorio.
- Controles tÃ¡ctiles de al menos 44 px e inputs de 16 px en mÃ³vil.
- Compatibilidad con `safe-area`.
- Estado de conexiÃ³n con `role="status"` y `aria-live`.
- Tabla desplazable horizontalmente en pantallas estrechas.
- Valores escapados antes de insertarlos mediante `innerHTML`.
- Sin frameworks ni dependencias externas.

## Aclaraciones del DiseÃ±o

- `app.js` no contiene `fetch` ni construye rutas de red.
- `LogicaFake` pertenece a `frontend_business_logic`, no al componente GUI.
- Si no se selecciona un tipo, la grÃ¡fica prioriza O3; las tarjetas muestran el valor mÃ¡s reciente de cada tipo dentro del conjunto recibido.
- La comprobaciÃ³n visual presencial complementa, pero no sustituye, las pruebas automatizadas.

## Reglas Generales

- **Lenguaje de ProgramaciÃ³n:** JavaScript sin frameworks, HTML5 y CSS3.
- **Encabezados de Funciones/MÃ©todos:** cada funciÃ³n JavaScript propia debe incluir diseÃ±o lÃ³gico entre `--------------------` y breve descripciÃ³n.
- **Legibilidad del CÃ³digo:** separar manipulaciÃ³n del DOM, presentaciÃ³n y llamadas a la interfaz de `frontend_business_logic`.
- **Pruebas Automatizadas:** `src/gui/tests/gui_unit_test.js` verifica transformaciones puras, escape, resumen y ausencia de comunicaciÃ³n directa.
