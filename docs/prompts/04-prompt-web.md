# Prompt · Web Sprint 0

```text
Genera web/index.html, web/css/styles.css, web/js/LogicaFake.js y web/js/app.js para PBIO GTI Joan.

La página usa api.php relativo. Debe comprobar health, cargar dispositivos y tipos, listar medidas con filtros dispositivo/tipo/desde/hasta, refrescar cada 5 segundos y mostrar: O3, temperatura, último RSSI, gráfica temporal y tabla histórica.

LogicaFake.js debe ser la única capa que hace fetch. Operaciones: listarMedidas(filtros), listarDispositivos(), listarTiposMedida(), health(). app.js controla DOM.

IDs: O3=14, Temperatura=12. Histórico: fecha, dispositivo, tipo, valor+unidad, RSSI. Escapa siempre valores antes de usar innerHTML.

Diseño responsive de aspecto profesional. En móvil pequeño ~375 CSS px: O3 y temperatura en dos columnas, RSSI en fila completa, controles >=44px, input/select font-size 16px, safe-area de iPhone, paddings compactos, tabla horizontal táctil, gráfica ~245px. El dibujo Canvas debe usar la altura real de getBoundingClientRect(), no asumir siempre 340px.

Añade estado accesible con role=status/aria-live.

Cabecera académica en HTML/CSS/JS y diseño lógico + breve descripción sobre cada función JS.

No uses frameworks ni librerías externas. Entrega archivos completos.
```
