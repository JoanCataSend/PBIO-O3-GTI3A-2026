# Prompt 6 - GUI web

```text
Implementa `src/gui/` según `doc/gui_design.md` con HTML5, CSS3 y JavaScript sin frameworks.

REGLA ARQUITECTÓNICA
La GUI solo puede consumir `LogicaFake` de `frontend_business_logic`. `src/gui/js/app.js` no puede contener fetch, XMLHttpRequest, WebSocket ni URLs del backend.

OPERACIONES CONSUMIDAS
probarConexion() --> estado: EstadoBD
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]

OPERACIONES PROPIAS DE APP.JS
iniciar()
cargarCatalogos()
actualizar()
valor: Text --> fechaSql() --> fecha: Text
medidas: [MedidaVista] --> pintarResumen()
medidas: [MedidaVista] --> pintarTabla()
medidas: [MedidaVista] --> pintarGrafica()
valor: Text --> escapar() --> texto_seguro: Text
mensaje: Text --> mostrarError()

PANTALLA
- estado de conexión accesible
- filtros de dispositivo/tipo/fecha
- tarjetas de O3, temperatura y RSSI
- gráfica Canvas
- tabla histórica
- botón y refresco cada 5 s

SEGURIDAD/UX
Escapar datos antes de innerHTML, responsive, controles táctiles >=44 px, inputs 16 px, safe-area y tabla desplazable.

TEST
`src/gui/tests/gui_unit_test.js` debe poder ejecutarse con Node y verificar funciones puras, resumen y ausencia de transporte directo.

Cada función JavaScript propia debe tener bloque `--------------------`, diseño lógico y descripción.
```
