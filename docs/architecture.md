# Arquitectura

## Flujo

```text
ULPSM-O3
   ↓
nRF52840
   ↓
ADC + cálculo + filtrado
   ↓
BLE / iBeacon
   ↓
Android
```

## Protocolo

UUID:

```text
EPSG-GTI-PROY-3A
```

Nombre BLE:

```text
GTI Joan
```

Codificación:

```text
Major = (ID << 8) | contador
Minor = valor
```

IDs relevantes:

```text
Temperatura = 12
O3 = 14
```

Ejemplo:

```text
ID O3 = 14
contador = 16
Major = 3600
Minor = 291
```

Android obtiene:

```text
ID = (Major >> 8) & 0xFF
contador = Major & 0xFF
```

y usa `Minor` como valor de medida.
