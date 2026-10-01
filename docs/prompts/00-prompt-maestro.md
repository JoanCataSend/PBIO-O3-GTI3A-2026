# Prompt maestro · Regenerar el Sprint 0 completo

Copia desde **PROMPT** hasta **FIN DEL PROMPT** en otra IA. Está escrito para que no necesite conocer previamente el proyecto.

---

## PROMPT

Actúa como ingeniero/a de software senior especializado/a en C++ embebido, Android Java, PHP, MariaDB y JavaScript. Debes generar **desde cero** un proyecto académico PBIO llamado **GTI Joan**, respetando exactamente la arquitectura y nombres indicados. No inventes nombres alternativos ni cambies requisitos.

### 1. Objetivo

Implementar un Sprint 0 reproducible en el que una placa SparkFun Pro nRF52840 Mini genera dos **medidas ficticias constantes**:

- O3 = `123 ppb`
- Temperatura = `-12 °C`

El flujo obligatorio es:

```text
firmware C++ -> BLE/iBeacon -> Android Java -> HTTPS/JSON -> API REST PHP
-> lógica de negocio PHP -> MariaDB -> API REST -> página web JavaScript
```

La medida inicial debe llegar con el mismo valor a la web.

### 2. Notación lógica que debes respetar

Usa estos tipos en comentarios/diseños:

```text
N = natural
Z = entero
R = real
VoF = booleano
Texto = cadena
[T] = lista/array
[T]_n = array fijo
```

Tipos:

```text
MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)

Dispositivo = (dispositivoId:N, uuid:Texto, nombre:Texto)
TipoMedida = (tipoMedidaId:N, nombre:Texto, unidad:Texto)

MedidaVista = (
    medidaId:N,
    dispositivoId:N,
    uuid:Texto,
    dispositivo:Texto,
    tipoMedidaId:N,
    tipoMedida:Texto,
    unidad:Texto,
    valor:Z,
    contador:N,
    rssi:Z,
    fechaHora:Texto
)
```

Formato de diseño de funciones:

```text
entrada:Tipo --> funcion() --> salida:Tipo | Error
```

Nunca describas punteros, callbacks, referencias o detalles de memoria como parte del diseño lógico.

### 3. Comentarios obligatorios

Cada fichero fuente creado por ti debe comenzar con una cabecera que contenga:

- nombre del fichero;
- descripción;
- `Copyright: 2026 Joan (uso académico PBIO - UPV)`;
- fecha `2026-10-01`;
- autor `Joan`;
- aportación.

Encima de **cada función o método** incluye:

- su diseño lógico con la notación anterior;
- una breve descripción.

No elimines estos comentarios por considerarlos redundantes: son requisito académico.

### 4. Firmware

Crear:

```text
firmware/NodoO3/NodoO3.ino
firmware/NodoO3/Medidor.h
firmware/NodoO3/Publicador.h
firmware/NodoO3/EmisoraBLE.h
```

Entorno:

- SparkFun Pro nRF52840 Mini;
- Adafruit nRF52 Boards 1.7.0;
- Bluefruit52Lib.

`Medidor` NO debe leer ningún ADC en Sprint 0.

```text
medirO3() --> 123
medirTemperatura() --> -12
```

BLE:

```text
nombre = "GTI Joan"
uuid ASCII = "EPSG-GTI-PROY-3A"
fabricante = 0x004C
intervalo advertising = 100 ms
anuncio = no conectable, escaneable
```

IDs:

```text
CO2 = 11
TEMPERATURA = 12
RUIDO = 13
O3 = 14
```

Protocolo:

```text
Major = (idMedida << 8) | contador
Minor = valor representado en 16 bits
```

Publicar O3 durante 1200 ms, esperar 300 ms, publicar temperatura durante 1200 ms y esperar 1500 ms.

### 5. Android

Crear proyecto Android Java en:

```text
android/
```

Configuración exacta:

```text
namespace/applicationId = org.jordi.prueba2025
compileSdk = 33
minSdk = 28
targetSdk = 32
Gradle Android plugin = 7.3.0
Gradle wrapper = 7.4
Java = 1.8 source/target
JDK recomendado para ejecutar Gradle = 17
```

Dependencias:

```text
com.android.support:appcompat-v7:28.0.0
junit:junit:4.13.2
com.android.support.test:runner:1.0.2
com.android.support.test.espresso:espresso-core:3.0.2
```

Clases obligatorias:

```text
MainActivity
TramaIBeacon
Utilidades
MedidaEntrada
LogicaFake
PeticionarioREST
```

La app debe:

1. pedir permisos BLE según versión Android;
2. buscar exactamente el dispositivo `GTI Joan`;
3. analizar manufacturer data iBeacon Apple `4C 00 02 15`;
4. validar UUID `EPSG-GTI-PROY-3A`;
5. obtener `idMedida` y `contador` de Major;
6. interpretar O3 como unsigned y temperatura como signed int16;
7. mostrar O3, temperatura, contador, RSSI y Major/ID/Minor;
8. evitar enviar varias veces el mismo contador para cada tipo;
9. crear `MedidaEntrada`;
10. llamar `LogicaFake.insertarMedida()`;
11. mostrar `Servidor: medida guardada` al recibir éxito.

URL exacta:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

La UI debe tener botones `Buscar GTI Joan` y `Detener búsqueda`.

### 6. MariaDB

Crear:

```text
server/database/schema.sql
server/database/seed.sql
server/database/drop.sql
```

Base lógica `jcatsen_pbio`.

Tablas:

```text
Dispositivo(
  dispositivoId INT UNSIGNED PK AUTO_INCREMENT,
  uuid VARCHAR(64) UNIQUE NOT NULL,
  nombre VARCHAR(100) NOT NULL
)

TipoMedida(
  tipoMedidaId INT UNSIGNED PK,
  nombre VARCHAR(50) UNIQUE NOT NULL,
  unidad VARCHAR(20) NOT NULL
)

Medida(
  medidaId BIGINT UNSIGNED PK AUTO_INCREMENT,
  dispositivoId INT UNSIGNED FK NOT NULL,
  tipoMedidaId INT UNSIGNED FK NOT NULL,
  valor INT NOT NULL,
  contador TINYINT UNSIGNED NOT NULL CHECK 0..255,
  rssi SMALLINT NOT NULL,
  fechaHora DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
)
```

Índices:

```text
(dispositivoId, fechaHora)
(tipoMedidaId, fechaHora)
```

Seed:

```text
EPSG-GTI-PROY-3A / GTI Joan
11 CO2 ppm
12 Temperatura °C
13 Ruido dB
14 O3 ppb
```

### 7. Lógica de negocio PHP

Crear:

```text
server/Logica.php
server/SDBaseDatos.example.php
```

No pongas contraseñas reales.

Funciones exactas:

```text
conexionBD()
probarConexion()
insertarMedida(array $datos)
buscarMedidaConId(int $medidaId)
listarMedidas(array $filtros = [])
listarDispositivos()
listarTiposMedida()
buscarUltimaMedida(int $dispositivoId, int $tipoMedidaId)
validarMedidaEntrada(array $datos)
consultaMedidaVista()
normalizarMedidaVista(array $fila)
```

`validarMedidaEntrada` debe exigir:

```text
uuid, tipoMedidaId, valor, contador, rssi
uuid Texto no vacío
campos numéricos enteros
0 <= contador <= 255
```

`insertarMedida` debe comprobar que el UUID y el tipo existen antes del INSERT.

La lógica PHP **no debe conocer códigos HTTP**.

### 8. API REST PHP

Crear:

```text
web/api.php
```

Debe resolver `Logica.php` tanto si `server/` está dentro de `web/` en despliegue como si es hermano de `web/` en el repositorio.

Rutas lógicas:

```text
POST api.php                      -> insertarMedida -> 201
GET  api.php                      -> listarMedidas -> 200
GET  api.php?accion=health        -> probarConexion
GET  api.php?accion=dispositivos  -> listarDispositivos
GET  api.php?accion=tipos         -> listarTiposMedida
GET  api.php?accion=ultima&dispositivoId=X&tipoMedidaId=Y
```

Errores:

```text
InvalidArgumentException -> 400
DomainException -> 404
método no GET/POST -> 405
Throwable -> 500 con mensaje genérico
```

### 9. Web

Crear:

```text
web/index.html
web/css/styles.css
web/js/LogicaFake.js
web/js/app.js
```

Requisitos:

- estado `Servidor conectado`;
- filtros por dispositivo, tipo, desde y hasta;
- tarjeta O3;
- tarjeta temperatura;
- último RSSI;
- gráfica temporal Canvas;
- histórico tabular;
- refresco automático cada 5 s;
- escapar datos antes de insertarlos en HTML;
- responsive escritorio/móvil;
- optimizado para 375 CSS px (iPhone 13 mini como referencia);
- controles táctiles >= 44 px;
- inputs/select de 16 px en móvil;
- safe-area;
- tabla con scroll horizontal;
- usar la altura CSS real del Canvas al dibujar.

La web debe acceder al servidor exclusivamente mediante `LogicaFake.js`.

### 10. Tests

Crear Android:

```text
ProtocolUnitTest.java
ServidorUnitTest.java
AppInstrumentedTest.java
```

Casos obligatorios:

```text
ID 14 + contador 16 -> Major 3600
FF F4 -> -12
00 7B -> 123
URL exacta HTTPS
ruta jcatsen.upv.edu.es/biometria
package = org.jordi.prueba2025
```

Crear PHP:

```text
server/tests/LogicaUnitTest.php
server/tests/ApiIntegracionTest.php
```

`LogicaUnitTest` debe comprobar validación, límites del contador, normalización y joins de la consulta.

`ApiIntegracionTest` debe ser de solo lectura y comprobar:

```text
health
base jcatsen_pbio
dispositivo GTI Joan
O3 ID14 ppb
Temperatura ID12
listado de medidas
```

No abras MariaDB a Internet para los tests.

### 11. Git / secretos

Crear `.gitignore` que ignore:

```text
.idea/
.gradle/
**/build/
local.properties
server/SDBaseDatos.php
*.apk
*.aab
binarios Arduino
temporales del sistema
```

No generes ni incluyas:

- contraseña de MariaDB;
- `android.zip` duplicado;
- `.git`;
- `.idea`;
- `.gradle`;
- `build`;
- `local.properties`.

### 12. Documentación

Genera README con:

- objetivo;
- arquitectura;
- estructura;
- ramas `main`, `master`, `develop`, `sensor-real-final`;
- firmware;
- Android;
- API;
- BBDD;
- despliegue Plesk en `https://jcatsen.upv.edu.es/biometria/`;
- cómo ejecutar todos los tests;
- criterio de aceptación.

No menciones el subdominio `pbio.jcatsen.upv.edu.es` ni Node.js como arquitectura actual.

### 13. Formato de respuesta

Entrega **todos los archivos completos**, agrupados por ruta. No uses fragmentos tipo “resto igual”. No dejes TODOs. No inventes credenciales. Al final incluye una tabla de verificación requisito -> archivo que lo cumple.

Antes de responder revisa coherencia entre nombres, tipos, IDs, URL y tests.

## FIN DEL PROMPT
