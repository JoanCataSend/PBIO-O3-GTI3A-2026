# PBIO · Sprint 0 · GTI Joan

Proyecto académico de **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**.

Esta rama representa el **Sprint 0 reproducible**: la placa no depende todavía de una lectura física del sensor, sino que genera medidas ficticias constantes para demostrar el funcionamiento completo de la arquitectura.

## Objetivo

La prueba debe conservar la misma medida de principio a fin:

```text
SparkFun nRF52840
(medida ficticia)
        ↓ BLE / iBeacon
      Android
        ↓ HTTPS / JSON
      API REST
        ↓
 Lógica de negocio
        ↓
      MariaDB
        ↓
    Página web
```

Valores utilizados:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

## Criterio de aceptación

El Sprint 0 se considera validado si los valores ficticios generados en el firmware aparecen con el mismo valor en:

1. Monitor Serie;
2. Android;
3. MariaDB;
4. web.

Evidencias disponibles en:

```text
docs/evidencias/
```

---

# Arquitectura

```text
Medidor
  ↓
Publicador
  ↓
EmisoraBLE
  ↓ iBeacon
MainActivity
  ↓
LogicaFake.java
  ↓
PeticionarioREST
  ↓ HTTPS
web/api.php
  ↓
server/Logica.php
  ↓
MariaDB
  ↑
web/api.php
  ↑
LogicaFake.js
  ↑
app.js / navegador
```

Documentación ampliada:

```text
docs/architecture.md
docs/disenos/
```

---

# Estructura del repositorio

```text
.
├── firmware/
│   └── NodoO3/
│       ├── NodoO3.ino
│       ├── Medidor.h
│       ├── Publicador.h
│       └── EmisoraBLE.h
│
├── android/
│   └── proyecto Android Studio
│
├── server/
│   ├── Logica.php
│   ├── SDBaseDatos.example.php
│   ├── database/
│   │   ├── schema.sql
│   │   ├── seed.sql
│   │   └── drop.sql
│   └── tests/
│       ├── LogicaUnitTest.php
│       └── ApiIntegracionTest.php
│
├── web/
│   ├── api.php
│   ├── index.html
│   ├── css/styles.css
│   └── js/
│       ├── LogicaFake.js
│       └── app.js
│
├── docs/
│   ├── disenos/
│   ├── prompts/
│   ├── evidencias/
│   ├── sensor-real-final/
│   ├── architecture.md
│   ├── api.md
│   ├── database.md
│   ├── deployment-plesk.md
│   ├── validation.md
│   ├── class-audit.md
│   └── rubrica-sprint0.md
│
├── scripts/
│   └── test-sprint0.ps1
│
├── .gitignore
└── README.md
```

No deben versionarse:

```text
.git/
.idea/
.gradle/
**/build/
local.properties
server/SDBaseDatos.php
android.zip
```

---

# Ramas

Estrategia utilizada:

```text
main              Sprint 0 reproducible
master            versión estable Sprint 0
develop           desarrollo
sensor-real-final evolución con sensor físico
```

La entrega Sprint 0 debe consultarse en `main`/`master`.

La adquisición física del sensor se conserva separada en `sensor-real-final` para no mezclarla con la demostración ficticia.

---

# Firmware

Placa:

```text
SparkFun Pro nRF52840 Mini
```

Entorno utilizado:

```text
Arduino IDE
Adafruit nRF52 Boards 1.7.0
Bluefruit52Lib
```

Archivo principal:

```text
firmware/NodoO3/NodoO3.ino
```

## Medidas Sprint 0

`Medidor.h` devuelve siempre:

```text
medirO3()          -> 123 ppb
medirTemperatura() -> -12 °C
```

No se realiza lectura ADC en esta rama.

## BLE / iBeacon

```text
Nombre: GTI Joan
UUID: EPSG-GTI-PROY-3A
Manufacturer: 0x004C
Advertising: no conectable, escaneable
Intervalo: 100 ms
```

IDs:

| Medida | ID |
|---|---:|
| CO2 | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O3 | 14 |

Codificación:

```text
Major = (idMedida << 8) | contador
Minor = valor de la medida en 16 bits
```

Ejemplo:

```text
ID O3 = 14
contador = 16
Major = 3600
Minor = 123
```

---

# Android

Proyecto:

```text
android/
```

Configuración actual:

```text
package / namespace = org.jordi.prueba2025
compileSdk = 33
minSdk = 28
targetSdk = 32
Android Gradle Plugin = 7.3.0
Gradle wrapper = 7.4
JDK recomendado = 17
```

Clases principales:

```text
MainActivity
TramaIBeacon
Utilidades
MedidaEntrada
LogicaFake
PeticionarioREST
```

La aplicación:

1. solicita permisos BLE;
2. busca `GTI Joan`;
3. valida iBeacon y UUID `EPSG-GTI-PROY-3A`;
4. obtiene ID y contador de Major;
5. interpreta O3 como unsigned y temperatura como signed int16;
6. muestra O3, temperatura, contador, RSSI y trama;
7. evita duplicados por contador;
8. envía `MedidaEntrada` al servidor.

URL exacta:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

La prueba BLE debe hacerse con un dispositivo Android físico o entorno que soporte BLE real.

---

# Base de datos

Motor:

```text
MariaDB
```

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
Dispositivo 1 ─── N Medida
TipoMedida  1 ─── N Medida
```

Scripts:

```text
server/database/schema.sql
server/database/seed.sql
server/database/drop.sql
```

La configuración real de acceso se guarda en:

```text
server/SDBaseDatos.php
```

pero ese fichero **no se versiona**. La plantilla pública es:

```text
server/SDBaseDatos.example.php
```

---

# Lógica de negocio

Archivo:

```text
server/Logica.php
```

Funciones principales:

```text
conexionBD()
probarConexion()
insertarMedida()
buscarMedidaConId()
listarMedidas()
listarDispositivos()
listarTiposMedida()
buscarUltimaMedida()
validarMedidaEntrada()
consultaMedidaVista()
normalizarMedidaVista()
```

La lógica no conoce códigos HTTP ni vistas.

Diseño formal:

```text
docs/disenos/03-logica-negocio.md
```

---

# API REST

Endpoint:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

## Health

```text
GET https://jcatsen.upv.edu.es/biometria/api.php?accion=health
```

Esperado:

```json
{"ok":true,"database":"jcatsen_pbio"}
```

## Dispositivos

```text
GET https://jcatsen.upv.edu.es/biometria/api.php?accion=dispositivos
```

## Tipos

```text
GET https://jcatsen.upv.edu.es/biometria/api.php?accion=tipos
```

## Listar medidas

```text
GET https://jcatsen.upv.edu.es/biometria/api.php
```

Filtros opcionales:

```text
dispositivoId
tipoMedidaId
desde
hasta
```

## Insertar medida

```text
POST https://jcatsen.upv.edu.es/biometria/api.php
Content-Type: application/json
```

Ejemplo:

```json
{
  "uuid": "EPSG-GTI-PROY-3A",
  "tipoMedidaId": 14,
  "valor": 123,
  "contador": 16,
  "rssi": -58
}
```

---

# Web

URL:

```text
https://jcatsen.upv.edu.es/biometria/
```

Funciones:

- últimas medidas de O3 y temperatura;
- último RSSI;
- filtros;
- histórico;
- gráfica temporal;
- refresco automático cada 5 s;
- estado de conexión;
- interfaz responsive.

La versión móvil está optimizada también para pantallas pequeñas (~375 CSS px): controles táctiles, safe-area, dos tarjetas prioritarias, tabla desplazable y Canvas adaptable.

---

# Despliegue Plesk

Estructura pública:

```text
httpdocs/
└── biometria/
    ├── index.html
    ├── api.php
    ├── css/
    ├── js/
    └── server/
        ├── Logica.php
        └── SDBaseDatos.php
```

Guía completa:

```text
docs/deployment-plesk.md
```

La arquitectura actual **no utiliza** `pbio.jcatsen.upv.edu.es` ni Node.js.

---

# Tests automáticos

## PHP · lógica de negocio

Desde la raíz en Windows/XAMPP:

```powershell
C:\xampp\php\php.exe server\tests\LogicaUnitTest.php
```

Resultado validado durante el desarrollo:

```text
Resultado: 10/10 tests correctos.
LOGICA UNIT TEST: OK
```

Comprueba:

- campos obligatorios;
- UUID válido;
- campos enteros;
- límites de contador;
- normalización de `MedidaVista`;
- estructura de la consulta con las tres tablas.

## PHP · integración API

```powershell
C:\xampp\php\php.exe server\tests\ApiIntegracionTest.php
```

Resultado validado:

```text
Resultado: 6/6 tests correctos.
API INTEGRACION TEST: OK
```

Comprueba de forma **solo lectura**:

- `health`;
- base `jcatsen_pbio`;
- dispositivo `GTI Joan`;
- O3 ID 14 / ppb;
- Temperatura ID 12;
- listado de medidas.

No se abre MariaDB a Internet para ejecutar este test.

## Android · unit tests

Desde `android/`:

```powershell
.\gradlew.bat test
```

Casos principales:

```text
ID14 + contador16 -> Major 3600
FF F4 -> -12
00 7B -> 123
URL API exacta
ruta /biometria
```

## Android · instrumented test

Con dispositivo/emulador conectado:

```powershell
.\gradlew.bat connectedAndroidTest
```

Comprueba el package instalado.

## Script conjunto Windows

Desde la raíz:

```powershell
.\scripts\test-sprint0.ps1
```

Ejecuta los dos tests PHP y los unit tests Android.

---

# Test manual de extremo a extremo

## 1. Firmware

Cargar `NodoO3.ino` y comprobar en Monitor Serie:

```text
O3 = 123 ppb
Temperatura = -12 C
```

## 2. Android

Pulsar `Buscar GTI Joan` y comprobar:

```text
O3: 123 ppb
Temperatura: -12 °C
Servidor: medida guardada
```

## 3. MariaDB

Comprobar nuevas filas de tipo:

```text
14 -> O3
12 -> Temperatura
```

## 4. Web

Abrir:

```text
https://jcatsen.upv.edu.es/biometria/
```

y comprobar:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

Procedimiento completo:

```text
docs/validation.md
```

---

# Diseños en notación de la asignatura

Carpeta:

```text
docs/disenos/
```

Contenido:

```text
00-notacion-y-tipos.md
01-firmware.md
02-android.md
03-logica-negocio.md
04-base-datos.md
05-api-rest.md
06-web.md
07-protocolo-y-flujo.md
08-tests-criterios.md
```

Los diseños usan tipos abstractos y firmas lógicas independientes del lenguaje.

---

# Prompts reproducibles de IA

Carpeta:

```text
docs/prompts/
```

Incluye un **prompt maestro** capaz de describir el proyecto completo a otra IA sin contexto previo y prompts separados para:

- firmware;
- Android;
- backend/BBDD/API;
- web;
- tests/documentación.

Los prompts especifican estructura, nombres, tipos, comentarios, tests, criterios de aceptación y restricciones.

---

# Relación con la rúbrica

Checklist:

```text
docs/rubrica-sprint0.md
```

Resume dónde está la evidencia de:

- buenas prácticas;
- ingeniería inversa y diseño;
- arquitectura/lógica;
- tests;
- prompts/uso de IA;
- test de funcionamiento.

---

# Versión con sensor real

La evolución posterior con lectura física de O3 y temperatura se conserva en la rama:

```text
sensor-real-final
```

La documentación específica que todavía aparece en esta copia del proyecto se encuentra aislada en:

```text
docs/sensor-real-final/
```

y **no describe el Sprint 0 ficticio**.
