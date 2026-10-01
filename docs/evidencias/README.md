# Evidencias del test de funcionamiento

Estas capturas documentan la prueba manual de extremo a extremo del Sprint 0.

## 1. Monitor Serie

Archivo: `01-monitor-serie.png`

Se observa la publicación de:

```text
O3 -> ID 14 -> minor 123
Temperatura -> ID 12 -> minor -12
```

## 2. Aplicación Android

Archivo: `02-android.jpeg`

Se observa:

```text
O3: 123 ppb
Temperatura: -12 °C
Servidor: medida guardada
```

## 3. Página web

Archivo: `03-web.png`

Se observan los mismos valores finales:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

## Cadena demostrada

```text
Firmware ficticio -> BLE/iBeacon -> Android -> HTTPS/API -> lógica -> MariaDB -> web
```

La captura de MariaDB puede añadirse aquí si se desea conservar también evidencia visual del almacenamiento intermedio.
