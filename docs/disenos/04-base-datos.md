# 04 · Diseño lógico de base de datos

La guía de notación aportada define tipos, tuplas, funciones y clases, pero no establece una notación relacional específica. Por ello, el modelo se expresa mediante **tuplas tipadas + claves + cardinalidades**, manteniendo la terminología de la asignatura.

## Entidades

```text
Dispositivo = (
    dispositivoId:N,
    uuid:Texto,
    nombre:Texto
)
```

Restricciones:

```text
dispositivoId PK
uuid UNIQUE
uuid != ""
```

```text
TipoMedida = (
    tipoMedidaId:N,
    nombre:Texto,
    unidad:Texto
)
```

Restricciones:

```text
tipoMedidaId PK
nombre UNIQUE
```

```text
Medida = (
    medidaId:N,
    dispositivoId:N,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z,
    fechaHora:Texto
)
```

Restricciones:

```text
medidaId PK
dispositivoId FK -> Dispositivo.dispositivoId
tipoMedidaId FK -> TipoMedida.tipoMedidaId
0 <= contador <= 255
```

## Cardinalidades

```text
Dispositivo 1 ---------------- N Medida
TipoMedida  1 ---------------- N Medida
```

Cada `Medida` pertenece exactamente a un `Dispositivo` y exactamente a un `TipoMedida`.

## Datos iniciales

```text
Dispositivo(
    uuid = "EPSG-GTI-PROY-3A",
    nombre = "GTI Joan"
)
```

```text
TipoMedida(11, "CO2",         "ppm")
TipoMedida(12, "Temperatura", "°C")
TipoMedida(13, "Ruido",       "dB")
TipoMedida(14, "O3",          "ppb")
```

## Índices

Para los listados temporales:

```text
(dispositivoId, fechaHora)
(tipoMedidaId, fechaHora)
```

## Correspondencia con SQL

```text
server/database/schema.sql
server/database/seed.sql
server/database/drop.sql
```
