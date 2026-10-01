# PBIO · Sprint 0 · GTI Joan

Proyecto de la asignatura **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**.

El objetivo del Sprint 0 es demostrar el funcionamiento completo de la arquitectura usando una **medida ficticia generada en la placa**, enviada por BLE al teléfono Android, almacenada mediante una API REST y la lógica de negocio en una base de datos MariaDB, y finalmente visualizada desde una página web.

---

# 1. Objetivo del Sprint 0

La prueba de funcionamiento consiste en introducir una medida ficticia en el código de la placa y comprobar que esa misma medida recorre todo el sistema:

```text
SparkFun nRF52840
(medida ficticia)
        ↓
   BLE / iBeacon
        ↓
      Android
        ↓
   HTTPS / JSON
        ↓
      API REST
        ↓
 Lógica de negocio
        ↓
      MariaDB
        ↓
    Página web
```

Para esta versión se utilizan medidas constantes:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Estas medidas permiten comprobar de forma reproducible toda la cadena sin depender todavía de la adquisición física del sensor.

---

# 2. Resultado esperado del test de funcionamiento

La misma medida ficticia debe aparecer en:

1. el monitor serie de Arduino;
2. la aplicación Android;
3. la base de datos MariaDB;
4. la página web.

Resultado esperado:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Si los valores introducidos en la placa aparecen finalmente en la web, se considera validado el funcionamiento completo del Sprint 0.

---

# 3. Arquitectura del sistema

La arquitectura utilizada separa las responsabilidades de cada parte del sistema:

```text
┌───────────────────────────┐
│ SparkFun nRF52840         │
│ Medidor + Publicador BLE  │
└─────────────┬─────────────┘
              │ iBeacon
              ▼
┌───────────────────────────┐
│ Aplicación Android        │
│ Recepción + lógica fake   │
└─────────────┬─────────────┘
              │ HTTPS / JSON
              ▼
┌───────────────────────────┐
│ API REST PHP              │
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Lógica de negocio         │
│ Logica.php                │
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ MariaDB                   │
│ Dispositivo               │
│ TipoMedida                │
│ Medida                    │
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│ Página web                │
│ Consulta y visualización  │
└───────────────────────────┘
```

---

# 4. Estructura del repositorio

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
│   ├── SDBaseDatos.php
│   ├── SDBaseDatos.example.php
│   ├── database/
│   └── tests/
│       ├── LogicaUnitTest.php
│       └── ApiIntegracionTest.php
│
├── web/
│   ├── api.php
│   ├── index.html
│   ├── css/
│   └── js/
│
├── docs/
│   ├── architecture.md
│   ├── database.md
│   ├── api.md
│   ├── deployment-plesk.md
│   ├── class-audit.md
│   └── validation.md
│
├── .gitignore
└── README.md
```

---

# 5. Ramas del repositorio

El repositorio utiliza varias ramas para separar el Sprint 0 del desarrollo posterior.

```text
main
master
develop
sensor-real-final
```

Uso de cada rama:

- `main`: versión del **Sprint 0** con medidas ficticias.
- `master`: versión estable del Sprint 0.
- `develop`: rama destinada al desarrollo.
- `sensor-real-final`: versión posterior con adquisición real del sensor de O₃ y temperatura.

La versión que debe utilizarse para comprobar el **Sprint 0** es:

```text
main
```

La versión avanzada con el sensor físico se conserva de forma independiente en:

```text
sensor-real-final
```

---

# 6. Firmware

## 6.1 Placa

Placa utilizada:

```text
SparkFun Pro nRF52840 Mini
```

Entorno:

```text
Arduino IDE
Adafruit nRF52 Boards 1.7.0
Bluefruit52Lib
Adafruit TinyUSB 3.6.0
```

Archivo principal:

```text
firmware/NodoO3/NodoO3.ino
```

---

## 6.2 Medidor del Sprint 0

En el Sprint 0, `Medidor.h` no realiza una adquisición analógica real.

Devuelve valores ficticios constantes:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Esto permite validar el funcionamiento completo del sistema independientemente del sensor físico.

---

# 7. Comunicación BLE

Nombre anunciado por el nodo:

```text
GTI Joan
```

UUID:

```text
EPSG-GTI-PROY-3A
```

Formato utilizado:

```text
Major = [ID de medida: 8 bits][contador: 8 bits]
Minor = valor de la medida
```

Identificadores utilizados:

| Medida | ID |
|---|---:|
| CO₂ | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O₃ | 14 |

En esta implementación se publican O₃ y temperatura.

Ejemplo:

```text
O₃
ID = 14
contador = 16

Major = (14 << 8) | 16
Major = 3600

Minor = 123
```

---

# 8. Aplicación Android

Proyecto:

```text
android/
```

Paquete:

```text
org.jordi.prueba2025
```

Se recomienda utilizar:

```text
JDK 17
```

La prueba BLE debe realizarse con un **teléfono Android físico**.

La aplicación:

1. busca el nodo `GTI Joan`;
2. valida el UUID `EPSG-GTI-PROY-3A`;
3. decodifica `Major` y `Minor`;
4. identifica el tipo de medida;
5. muestra O₃ y temperatura;
6. envía cada nueva medida al servidor mediante HTTPS.

URL utilizada por Android:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

---

# 9. API REST

La API está desplegada en:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

La API recibe las medidas desde Android y utiliza la lógica de negocio para acceder a la base de datos.

---

## 9.1 Health

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=health
```

Respuesta esperada:

```json
{
  "ok": true,
  "database": "jcatsen_pbio"
}
```

---

## 9.2 Tipos de medida

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=tipos
```

Tipos utilizados:

```text
11 → CO2
12 → Temperatura
13 → Ruido
14 → O3
```

---

## 9.3 Dispositivos

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=dispositivos
```

Dispositivo del proyecto:

```text
uuid   = EPSG-GTI-PROY-3A
nombre = GTI Joan
```

---

# 10. Lógica de negocio

La lógica del servidor está implementada en:

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

La lógica de negocio se mantiene separada de la interfaz HTTP.

---

# 11. Base de datos

Sistema utilizado:

```text
MariaDB
```

Base de datos:

```text
jcatsen_pbio
```

Tablas:

```text
Dispositivo
TipoMedida
Medida
```

Relaciones principales:

```text
Dispositivo 1 ───── N Medida
TipoMedida  1 ───── N Medida
```

La tabla `Medida` almacena:

```text
medidaId
dispositivoId
tipoMedidaId
valor
contador
rssi
fechaHora
```

Los scripts de creación e inicialización se encuentran en:

```text
server/database/
```

Las credenciales reales del servidor **no deben almacenarse en GitHub**.

Se utiliza:

```text
server/SDBaseDatos.php
```

en el servidor real, mientras que el repositorio mantiene un fichero de ejemplo:

```text
server/SDBaseDatos.example.php
```

---

# 12. Página web

Página principal:

```text
https://jcatsen.upv.edu.es/biometria/
```

La web consulta la API REST y permite:

- visualizar la última medida de O₃;
- visualizar la última temperatura;
- mostrar el último RSSI;
- filtrar mediciones;
- consultar el histórico;
- representar la evolución temporal;
- comprobar el estado del servidor.

La interfaz también está adaptada a dispositivos móviles.

---

# 13. Despliegue

El sistema web está desplegado mediante Plesk bajo:

```text
https://jcatsen.upv.edu.es/biometria/
```

Estructura utilizada en el servidor:

```text
httpdocs/
└── biometria/
    ├── api.php
    ├── index.html
    ├── css/
    ├── js/
    └── server/
```

La configuración privada de conexión con MariaDB se completa únicamente en el servidor.

---

# 14. Tests automáticos

El proyecto incluye tests automáticos para Android, protocolo, servidor, lógica de negocio e integración con la API.

---

## 14.1 Tests Android

Desde:

```text
android/
```

En Windows:

```powershell
gradlew.bat test
```

En Linux/macOS:

```bash
./gradlew test
```

Tests incluidos:

```text
ProtocolUnitTest.java
ServidorUnitTest.java
ExampleInstrumentedTest.java
```

---

## 14.2 ProtocolUnitTest

Comprueba:

- codificación del ID de medida y contador dentro de `Major`;
- decodificación de valores negativos de `Minor`.

Ejemplo comprobado:

```text
ID = 14
contador = 16
Major = 3600
```

También se comprueba la decodificación de una temperatura negativa.

---

## 14.3 ServidorUnitTest

Comprueba que la aplicación Android utiliza la URL correcta del servidor:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

---

# 15. Tests automáticos de la lógica de negocio

Archivo:

```text
server/tests/LogicaUnitTest.php
```

Desde la raíz del repositorio, en Windows con XAMPP:

```powershell
C:\xampp\php\php.exe server\tests\LogicaUnitTest.php
```

Este test comprueba automáticamente:

- validación de `MedidaEntrada`;
- presencia de campos obligatorios;
- UUID no vacío;
- validación de campos enteros;
- rango del contador entre 0 y 255;
- aceptación de los límites 0 y 255;
- normalización de `MedidaVista`;
- conversión de campos numéricos a enteros;
- conservación de los valores;
- estructura de la consulta que relaciona `Medida`, `Dispositivo` y `TipoMedida`.

Resultado obtenido:

```text
[OK] Una MedidaEntrada valida supera la validacion
[OK] Se rechaza una medida sin uuid
[OK] Se rechaza un uuid vacio
[OK] Se rechaza un valor que no es entero
[OK] Se rechaza contador menor que 0
[OK] Se rechaza contador mayor que 255
[OK] Los contadores 0 y 255 son validos
[OK] MedidaVista normaliza los campos numericos a enteros
[OK] MedidaVista conserva correctamente los valores numericos
[OK] La consulta MedidaVista utiliza las tres tablas del diseño

Resultado: 10/10 tests correctos.
LOGICA UNIT TEST: OK
```

---

# 16. Test automático de integración

Archivo:

```text
server/tests/ApiIntegracionTest.php
```

Ejecutar desde la raíz del repositorio:

```powershell
C:\xampp\php\php.exe server\tests\ApiIntegracionTest.php
```

Este test comprueba automáticamente la integración real:

```text
Cliente
   ↓ HTTPS
API REST
   ↓
Lógica de negocio
   ↓
MariaDB
```

Comprueba:

- endpoint `health`;
- conexión con la base de datos `jcatsen_pbio`;
- existencia del dispositivo `GTI Joan`;
- UUID `EPSG-GTI-PROY-3A`;
- tipo O₃ con ID 14 y unidad `ppb`;
- tipo Temperatura con ID 12;
- listado de medidas.

Resultado obtenido:

```text
[OK] El endpoint health responde correctamente
[OK] La API confirma la base de datos jcatsen_pbio
[OK] La API devuelve el dispositivo GTI Joan
[OK] La API devuelve O3 con ID 14 y unidad ppb
[OK] La API devuelve Temperatura con ID 12
[OK] La API devuelve una coleccion de medidas

Resultado: 6/6 tests correctos.
API INTEGRACION TEST: OK
```

Este test no inserta ni elimina medidas.

---

# 17. Test manual completo del Sprint 0

Para comprobar todo el sistema:

## Paso 1

Compilar y cargar el firmware en la placa.

---

## Paso 2

Abrir el monitor serie.

Debe aparecer:

```text
O3 = 123 ppb
Temperatura = -12 C
```

---

## Paso 3

Abrir la aplicación Android en un teléfono físico.

Debe aparecer:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Además, Android debe indicar que la medida ha sido enviada o guardada en el servidor.

---

## Paso 4

Comprobar MariaDB.

La tabla:

```text
Medida
```

debe contener las nuevas mediciones recibidas.

---

## Paso 5

Abrir:

```text
https://jcatsen.upv.edu.es/biometria/
```

La web debe mostrar:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

---

# 18. Criterio de aceptación del Sprint 0

El Sprint 0 se considera validado si:

```text
Medida ficticia
      ↓
Arduino
      ↓
BLE
      ↓
Android
      ↓
HTTPS POST
      ↓
API REST
      ↓
Lógica de negocio
      ↓
MariaDB
      ↓
HTTPS GET
      ↓
Página web
```

mantiene correctamente los valores:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

de principio a fin.

---

# 19. Documentación del proyecto

La carpeta:

```text
docs/
```

contiene la documentación técnica.

Archivos principales:

- `architecture.md`: arquitectura y flujo completo.
- `database.md`: diseño de la base de datos.
- `api.md`: diseño del API REST.
- `deployment-plesk.md`: despliegue.
- `class-audit.md`: correspondencia entre diseño y código.
- `validation.md`: validaciones y criterios de aceptación.

---

# 20. Estado actual

## Sprint 0

```text
Medidas ficticias Arduino ✅
BLE / iBeacon ✅
Android ✅
API REST ✅
Lógica de negocio ✅
MariaDB ✅
Página web ✅
Tests unitarios lógica 10/10 ✅
Test integración API 6/6 ✅
```

---

## Versión con sensor real

La evolución posterior del proyecto, con adquisición física de O₃ y temperatura, está conservada en:

```text
sensor-real-final
```

Esta rama se mantiene separada para no modificar la versión entregable y reproducible del Sprint 0.
