# 07 · Protocolo BLE y flujo completo

## Mensaje BLE lógico

Cada advertising iBeacon transporta:

```text
IBEACON
<uuid:[N]_16>
<major:[N]_2>
<minor:[N]_2>
<txPower:Z>
```

Para PBIO:

```text
uuid = ASCII("EPSG-GTI-PROY-3A")
major = (idMedida << 8) OR contador
minor = valor representado en 16 bits
```

## Decodificación de Major

```text
major:N --> decodificarMajor() --> idMedida:N, contador:N

idMedida <- (major >> 8) AND 255
contador <- major AND 255
```

## Decodificación de Minor

Para O3:

```text
minor:[N]_2 --> bytesToUnsignedInt() --> valor:N
```

Para temperatura:

```text
minor:[N]_2 --> bytesToSignedInt16() --> valor:Z
```

## Ejemplo O3 Sprint 0

```text
idMedida = 14
contador = 16
valor = 123

major = 3600
minor = 123
```

## Ejemplo temperatura Sprint 0

```text
idMedida = 12
contador = 16
valor = -12

major = 3088
minor = representación de -12 en 16 bits
```

## Interacción completa

```text
Medidor          Publicador          Android            API           Lógica          BBDD          Web
  |                  |                  |                |               |              |             |
  | 123              |                  |                |               |              |             |
  |----------------->|                  |                |               |              |             |
  |                  | iBeacon O3       |                |               |              |             |
  |                  |----------------->|                |               |              |             |
  |                  |                  | POST medida    |               |              |             |
  |                  |                  |--------------->| insertar      |              |             |
  |                  |                  |                |-------------->| INSERT       |             |
  |                  |                  |                |               |------------->|             |
  |                  |                  |                |               |<-------------|             |
  |                  |                  |<---------------| 201           |              |             |
  |                  |                  |                |               |              |             |
  |                  |                  |                |               |              | GET         |
  |                  |                  |                |<--------------------------------------------|
  |                  |                  |                | listar        |              |             |
  |                  |                  |                |-------------->| SELECT       |             |
  |                  |                  |                |               |------------->|             |
  |                  |                  |                |               |<-------------|             |
  |                  |                  |                |-------------------------------------------->|
```

## Criterio de aceptación

```text
valor generado en Medidor = valor mostrado en Android = valor guardado = valor mostrado en web
```

Para la demostración:

```text
O3 = 123 ppb
Temperatura = -12 °C
```
