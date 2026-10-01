# Validación y criterio de aceptación del Sprint 0

## Criterio principal

El test se supera cuando **la misma medida ficticia definida en el firmware** atraviesa toda la arquitectura y aparece en la web.

Valores de referencia:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

## 1. Firmware

1. Compilar `firmware/NodoO3/NodoO3.ino`.
2. Cargarlo en la SparkFun Pro nRF52840 Mini.
3. Abrir el Monitor Serie a `115200`.
4. Comprobar mensajes equivalentes a:

```text
--- MEDIDA FICTICIA O3 ---
O3 = 123 ppb

--- PUBLICACION iBeacon ---
ID medida = 14
minor = 123

--- MEDIDA FICTICIA TEMPERATURA ---
Temperatura = -12 C

--- PUBLICACION iBeacon ---
ID medida = 12
minor = -12
```

## 2. Android

1. Instalar la aplicación en un teléfono Android físico.
2. Conceder permisos BLE.
3. Pulsar `Buscar GTI Joan`.
4. Comprobar:

```text
O3: 123 ppb
Temperatura: -12 °C
Servidor: medida guardada
```

## 3. API / base de datos

Health:

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=health
```

Esperado:

```json
{"ok":true,"database":"jcatsen_pbio"}
```

Comprobar en phpMyAdmin que `Medida` recibe filas para:

```text
12 -> Temperatura
14 -> O3
```

## 4. Web

Abrir:

```text
https://jcatsen.upv.edu.es/biometria/
```

Comprobar:

- O3 = `123 ppb`;
- Temperatura = `-12 °C`;
- RSSI visible;
- filtros operativos;
- histórico;
- gráfica;
- estado `Servidor conectado`.

## 5. Tests automáticos

### Lógica de negocio

```powershell
C:\xampp\php\php.exe server\tests\LogicaUnitTest.php
```

Resultado validado:

```text
10/10 tests correctos
```

### Integración API

```powershell
C:\xampp\php\php.exe server\tests\ApiIntegracionTest.php
```

Resultado validado:

```text
6/6 tests correctos
```

### Android unit tests

Desde `android/`:

```powershell
.\gradlew.bat test
```

### Android instrumented test

Con teléfono/emulador disponible:

```powershell
.\gradlew.bat connectedAndroidTest
```

## Evidencias

Las capturas disponibles se encuentran en `docs/evidencias/`.
