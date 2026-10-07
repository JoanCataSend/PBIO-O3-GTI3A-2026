# Prompt 6 - UX del navegador

```text
Implementa la UX web de PBIO en `src/web/` usando HTML5, CSS3 y JavaScript sin frameworks. La obtención de datos DEBE hacerse solo a través de `LogicaFake.js`; `app.js` no usa fetch.

PANTALLA
- Cabecera PBIO / Monitor ambiental GTI Joan.
- Estado de conexión accesible (`role=status`, `aria-live=polite`).
- Filtros: dispositivo, tipo, desde, hasta y botón Actualizar.
- Tarjetas: último O3 (ppb), Temperatura (°C) y RSSI (dBm).
- Gráfica temporal Canvas.
- Tabla: fecha/hora, dispositivo, tipo, valor+unidad, RSSI.
- Refresco automático cada 5 s.

CONTRATOS APP.JS
iniciar() -->
cargarCatalogos() -->
actualizar() -->
valor: Text --> fechaSql() --> fecha: Text
medidas: [MedidaVista] --> pintarResumen() -->
medidas: [MedidaVista] --> pintarTabla() -->
medidas: [MedidaVista] --> pintarGrafica() -->
valor: Text --> escapar() --> texto_seguro: Text
mensaje: Text --> mostrarError() -->

SEGURIDAD Y UX
- Escapar cualquier dato antes de insertarlo con innerHTML.
- Responsive desde ~375 CSS px.
- Controles táctiles >=44 px e inputs de 16 px en móvil.
- Safe-area para iPhone/notch.
- Tabla con scroll horizontal táctil.
- Canvas adaptado a getBoundingClientRect().
- Sin librerías externas.

COMENTARIOS
Cabecera de todos los archivos con Joan Catala Sendra. Cada función JS con `--------------------`, diseño lógico y descripción.

TESTS
Crear/actualizar `src/web/tests/web_unit_test.js` para probar `fechaSql()`, escape HTML y contrato de API relativa. El test debe ejecutarse con Node sin navegador real.

Entrega index.html, css/styles.css, js/app.js y el test completo. No dupliques la lógica REST en app.js.
```
