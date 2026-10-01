# Diseño de la base de datos

Motor desplegado: **MariaDB 10.x** en Plesk.

Base:

```text
jcatsen_pbio
```

## Esquema lógico

```text
Dispositivo = (
    dispositivoId:N,
    uuid:Texto,
    nombre:Texto
)

TipoMedida = (
    tipoMedidaId:N,
    nombre:Texto,
    unidad:Texto
)

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

## Relaciones

```text
Dispositivo 1 ───── N Medida
TipoMedida  1 ───── N Medida
```

Claves:

```text
Dispositivo.dispositivoId  PK
Dispositivo.uuid           UNIQUE

TipoMedida.tipoMedidaId    PK
TipoMedida.nombre          UNIQUE

Medida.medidaId            PK
Medida.dispositivoId       FK -> Dispositivo.dispositivoId
Medida.tipoMedidaId        FK -> TipoMedida.tipoMedidaId
```

Precondición de `contador`:

```text
0 <= contador <= 255
```

## Catálogo inicial

| ID | Tipo | Unidad |
|---:|---|---|
| 11 | CO2 | ppm |
| 12 | Temperatura | °C |
| 13 | Ruido | dB |
| 14 | O3 | ppb |

Dispositivo del Sprint 0:

```text
uuid = EPSG-GTI-PROY-3A
nombre = GTI Joan
```

## Reproducibilidad

Scripts:

```text
server/database/schema.sql
server/database/seed.sql
server/database/drop.sql
```

El esquema crea claves foráneas e índices por dispositivo/fecha y tipo/fecha.

Diseño formal ampliado: `docs/disenos/04-base-datos.md`.
