# Base de datos

Motor desplegado: **MariaDB** en Plesk.

Base:

```text
jcatsen_pbio
```

Tablas:

```text
Dispositivo
TipoMedida
Medida
```

Relaciones:

```text
Dispositivo 1:N Medida
TipoMedida  1:N Medida
```

Tipos de medida cargados:

| ID | Tipo | Unidad |
|---:|---|---|
| 11 | CO2 | ppm |
| 12 | Temperatura | °C |
| 13 | Ruido | dB |
| 14 | O3 | ppb |

Dispositivo:

```text
uuid = EPSG-GTI-PROY-3A
nombre = GTI Joan
```

Los scripts reproducibles están en `server/database/`.
