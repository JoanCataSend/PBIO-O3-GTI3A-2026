# android_design.md

## Component Design (Diseño del Componente)

**Componente:** `android`
**Implementación canónica para revisión:** `src/android/`
**Código operativo equivalente:** `android/`

La aplicación Android detecta el iBeacon del nodo `GTI Joan`, valida el UUID del proyecto, decodifica `Major` y `Minor`, evita reenvíos duplicados por contador y envía cada medida aceptada a la API REST.


### Tipos lógicos comunes

```text
N      número natural
Z      número entero
R      número real
VoF    booleano
Texto  cadena de caracteres
[T]    colección de T
[T]_n  array de T de tamaño fijo n
JSON   Texto con estructura JSON
```

Tipos del dominio:

```text
MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)

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


### Arquitectura

```text
MainActivity
   |
   +--> TramaIBeacon
   +--> Utilidades
   +--> MedidaEntrada
   +--> LogicaFake
             |
             +--> PeticionarioREST
```

### Contratos

```text
bytes:[N] --> TramaIBeacon() -->
analizar() -->
esValida() --> valida:VoF
getUUID() --> uuid:[N]_16
getMajor() --> major:[N]_2
getMinor() --> minor:[N]_2
getTxPower() --> txPower:Z

bytes:[N] --> bytesToString() --> Texto
bytes:[N] --> bytesToHexString() --> Texto
bytes:[N] --> bytesToUnsignedInt() --> N
bytes:[N]_2 --> bytesToSignedInt16() --> Z

MedidaEntrada(uuid:Texto, tipoMedidaId:N, valor:Z, contador:N, rssi:Z) -->
MedidaEntrada --> toJson() --> JSON | Error

datos:MedidaEntrada --> insertarMedida() --> MedidaVista | Error
url:Texto, datos:JSON --> postJson() --> JSON | Error
```

### Decodificación BLE

```text
si no existe cabecera iBeacon 4C 00 02 15 -> descartar
si UUID != "EPSG-GTI-PROY-3A" -> descartar
major <- bytesToUnsignedInt(majorBytes)
idMedida <- (major >> 8) AND 255
contador <- major AND 255
si idMedida = 14 -> valor <- bytesToUnsignedInt(minorBytes)
si no -> valor <- bytesToSignedInt16(minorBytes)
si idMedida no es 14 ni 12 -> no enviar
si contador ya enviado para ese tipo -> no enviar
crear MedidaEntrada y POST a la API
si el POST falla -> liberar contador para permitir reintento
```

Configuración de proyecto: `compileSdk 33`, `minSdk 28`, `targetSdk 32`, Gradle 7.4 y Java/JDK 17 para ejecutar el wrapper de este proyecto.

### Descripción textual de la GUI

La aplicación tiene una única pantalla principal (`activity_main.xml`) dentro de un `ScrollView`. De arriba abajo muestra: título **Nodo sensor GTI Joan**, estado de Bluetooth, botón **Buscar GTI Joan**, botón **Detener búsqueda**, valor destacado de O3 en ppb, temperatura en °C, contador, RSSI, estado del servidor y texto de `Major / Minor`. La pantalla debe permitir demostrar visualmente recepción BLE y confirmación de guardado sin navegar a otras vistas.

### Archivos del componente

```text
app/src/main/java/org/jordi/prueba2025/MainActivity.java
app/src/main/java/org/jordi/prueba2025/TramaIBeacon.java
app/src/main/java/org/jordi/prueba2025/Utilidades.java
app/src/main/java/org/jordi/prueba2025/MedidaEntrada.java
app/src/main/java/org/jordi/prueba2025/LogicaFake.java
app/src/main/java/org/jordi/prueba2025/PeticionarioREST.java
app/src/main/AndroidManifest.xml
app/src/main/res/layout/activity_main.xml
```

## Design Clarifications (Aclaraciones del Diseño)

- El escaneo BLE debe probarse en teléfono físico; un AVD puede no exponer un escáner BLE real.
- `LogicaFake` es un adaptador de cliente: separa la actividad de los detalles HTTP, aunque use el nombre histórico “Fake”.
- URL canónica: `https://jcatsen.upv.edu.es/biometria/api.php`.
- Los permisos contemplan Android 11 y Android 12+ y el manifest declara acceso a Internet y BLE.
- Los callbacks son un detalle de implementación; el contrato lógico de `postJson` se expresa como entrada JSON y salida JSON/error.
- El wrapper de Gradle y los recursos de launcher generados por Android Studio son infraestructura generada; la regla de cabeceras lógicas se aplica al código de aplicación y de tests mantenido por el proyecto.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** Java para la lógica Android y XML para manifest, layouts, recursos y temas.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** cada función, método y callback relevante incluye un bloque de comentario con diseño lógico, delimitado por `--------------------`.
- **Code Readability / Legibilidad del Código:** la actividad coordina UI/escaneo; parsing, utilidades, serialización y HTTP se separan en clases específicas.
- **Automated Testing / Pruebas Automatizadas:** `src/android/app/src/test/.../ProtocolUnitTest.java` valida Major, O3 y temperatura; `ServidorUnitTest.java` valida la URL; `AppInstrumentedTest.java` valida el paquete en dispositivo/emulador. El proyecto se ejecuta con `gradlew.bat test` usando JDK 17.
- **Source correspondence / Correspondencia:** `src/android/` reproduce el proyecto Android operativo completo para que diseño e implementación puedan compararse directamente.
