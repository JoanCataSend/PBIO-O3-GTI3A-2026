# Parámetros y calibración del sensor

## Unidad concreta

Data Matrix leído en la unidad utilizada:

```text
032824010609 110406 O3 2404 -74.04
```

Parámetros empleados:

```text
Sensibilidad individual = -74.04 nA/ppm
TIA Gain = 499 kV/A
```

Por tanto:

```text
M = -74.04 × 499 × 10^-6
M = -0.03694596 V/ppm
```

El firmware usa:

```text
M_V_PPM = -0.03694596
```

## Cero

El código actual usa:

```text
Voffset = 0
Vgas0 = Vref
```

y:

```text
DeltaV = Vgas - Vgas0
O3_RAW = DeltaV / M
```

Esto sirve para una comprobación funcional del prototipo, pero no equivale a una calibración absoluta en una atmósfera patrón.

## Filtrado

La estabilización de O₃ usa:

```text
mediana de 5 medidas
↓
EMA con alpha = 0.25
```

El filtrado reduce picos en la lectura mostrada, pero no mejora por sí solo la exactitud absoluta.
