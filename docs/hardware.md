# Hardware

## Componentes

- SparkFun Pro nRF52840 Mini
- SPEC Sensors ULPSM-O3

## Conexiones

| ULPSM-O3 | nRF52840 |
|---|---|
| `Vgas` | pin 5 / P0.05 / AIN3 |
| `Vref` | pin 28 / P0.28 / AIN4 |
| `Vtemp` | pin 29 / P0.29 / AIN5 |
| `GND` | GND |
| `V+` | 3V3 |

Los dos pines `V+` del módulo deben ir al mismo 3V3.

## ADC

El firmware usa:

```text
12 bits
oversampling = 16
50 lecturas promediadas por canal
```

Antes de promediar cada canal se descarta una lectura tras el cambio de entrada.

## Temperatura

El firmware estima:

```text
V+ ≈ 2 × Vref
```

y calcula la temperatura con la ecuación implementada en `Medidor.h`.
