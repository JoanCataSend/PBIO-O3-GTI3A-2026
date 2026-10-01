# 06 · Diseño lógico de la página web

## Arquitectura

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

La UI no construye SQL ni conoce la base de datos.

## LogicaFake.js

```text
url:Texto, opciones:PeticionHTTP
    --> pedir()
    --> JSON | Error

filtros:FiltrosMedida
    --> listarMedidas()
    --> [MedidaVista] | Error

listarDispositivos()
    --> [Dispositivo] | Error

listarTiposMedida()
    --> [TipoMedida] | Error

health()
    --> EstadoBD | Error
```

## app.js

```text
iniciar() -->
cargarCatalogos() -->
actualizar() -->
valor:Texto --> fechaSql() --> fecha:Texto
medidas:[MedidaVista] --> pintarResumen() -->
medidas:[MedidaVista] --> pintarTabla() -->
medidas:[MedidaVista] --> pintarGrafica() -->
valor:Texto --> escapar() --> textoSeguro:Texto
error:Error --> mostrarError() -->
```

## Algoritmo `iniciar()`

```text
registrar botón Actualizar

health()
si error
    mostrarError()
    terminar

marcar servidor conectado
cargarCatalogos()
actualizar()
programar actualizar() cada 5 segundos
```

## Algoritmo `actualizar()`

```text
filtros <- valores actuales de la UI
medidas <- listarMedidas(filtros)

pintarResumen(medidas)
pintarTabla(medidas)
pintarGrafica(medidas)
actualizar hora de refresco
marcar servidor conectado
```

## Requisitos UX implementados

- Responsive hasta pantallas pequeñas (~375 CSS px).
- Controles táctiles de al menos 44 px.
- Inputs de 16 px en móvil para evitar zoom automático de Safari iOS.
- `safe-area` para dispositivos con notch.
- Tabla con desplazamiento horizontal táctil.
- Estado de conexión visible.
- O3 y temperatura priorizados visualmente.
- Gráfica adaptada a la altura CSS real del dispositivo.
- Contenido del histórico escapado antes de insertarse en HTML.
