# PBIO · Sprint 0 · GTI Joan

Proyecto de la asignatura **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**.

El objetivo del Sprint 0 es demostrar el funcionamiento completo de la arquitectura usando una **medida ficticia generada en la placa**, enviada por BLE al teléfono Android, almacenada mediante una API REST y la lógica de negocio en una base de datos MariaDB, y finalmente visualizada desde una página web.

## Flujo del sistema

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

## Test de funcionamiento del Sprint 0

La versión de Sprint 0 utiliza medidas constantes en `Medidor.h`:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Estas medidas se publican mediante iBeacon y deben aparecer con el mismo valor en:

1. el monitor serie de Arduino;
2. la aplicación Android;
3. la tabla `Medida` de MariaDB;
4. la página web.

Esto permite comprobar de forma reproducible toda la cadena de comunicación sin depender todavía de la medida física del sensor.

## Estructura del repositorio

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
│   └── database/
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

## Ramas

El repositorio mantiene varias ramas para separar la entrega del Sprint 0 del desarrollo posterior:

- `main`: versión del **Sprint 0** con medidas ficticias.
- `master`: versión estable del Sprint 0.
- `develop`: rama destinada al desarrollo.
- `sensor-real-final`: versión posterior con adquisición real del sensor de O₃ y temperatura.

La versión que debe utilizarse para comprobar el **Sprint 0** es `main`.

## Identificación BLE

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

Identificadores:

| Medida | ID |
|---|---:|
| CO₂ | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O₃ | 14 |

En esta implementación se publican O₃ y temperatura.

## Firmware

Abrir en Arduino IDE:

```text
firmware/NodoO3/NodoO3.ino
```

Configuración utilizada:

- SparkFun Pro nRF52840 Mini
- Adafruit nRF52 Boards `1.7.0`
- Bluefruit52Lib
- Adafruit TinyUSB `3.6.0`

Para el Sprint 0, `Medidor.h` no realiza una adquisición analógica real: devuelve los valores ficticios definidos para la prueba.

## Android

Abrir en Android Studio:

```text
android/
```

Paquete de la aplicación:

```text
org.jordi.prueba2025
```

Se recomienda utilizar **JDK 17** con la versión de Gradle incluida.

La prueba BLE debe realizarse con un **teléfono Android físico**.

La aplicación:

1. busca el nodo `GTI Joan`;
2. valida el UUID `EPSG-GTI-PROY-3A`;
3. decodifica `Major` y `Minor`;
4. identifica el tipo de medida;
5. muestra O₃ y temperatura;
6. envía cada nueva medida al servidor mediante HTTPS.

## Servidor y API REST

API desplegada en:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

Comprobación de estado:

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=health
```

Debe devolver una respuesta indicando que la conexión con la base de datos es correcta.

Tipos de medida:

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=tipos
```

Dispositivos:

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=dispositivos
```

El servidor utiliza PHP y separa el acceso HTTP de la lógica de negocio.

## Base de datos

Sistema:

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
Dispositivo 1 ─── N Medida
TipoMedida  1 ─── N Medida
```

Los scripts de creación e inicialización se encuentran en:

```text
server/database/
```

Las credenciales reales del servidor **no deben almacenarse en GitHub**. El fichero con datos sensibles se mantiene fuera del control de versiones y se proporciona un fichero de ejemplo para indicar su estructura.

## Página web

La interfaz está disponible en:

```text
https://jcatsen.upv.edu.es/biometria/
```

La web consulta la API REST y permite:

- ver las últimas medidas;
- consultar O₃ y temperatura;
- visualizar el último RSSI;
- filtrar las mediciones;
- representar la evolución temporal;
- consultar el histórico almacenado.

La interfaz está adaptada también a dispositivos móviles.

## Cómo ejecutar los tests

### Tests unitarios de Android

Desde la carpeta:

```text
android/
```

en Windows:

```text
gradlew.bat test
```

En Linux/macOS:

```text
./gradlew test
```

Los tests unitarios incluidos comprueban aspectos del protocolo y de la configuración del servidor.

### Test de integración del Sprint 0

1. Compilar y cargar el firmware en la SparkFun.
2. Abrir el monitor serie.
3. Comprobar que se genera:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

4. Abrir la aplicación Android en un teléfono físico.
5. Comprobar que aparecen los mismos valores.
6. Verificar que Android informa de que la medida ha sido guardada.
7. Comprobar en MariaDB que existen nuevas filas en `Medida`.
8. Abrir:

```text
https://jcatsen.upv.edu.es/biometria/
```

9. Comprobar que la web muestra:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

Si el mismo valor ficticio generado al inicio aparece al final de la cadena, la prueba completa del Sprint 0 es correcta.

## Despliegue

La aplicación web y el backend PHP se encuentran desplegados bajo:

```text
https://jcatsen.upv.edu.es/biometria/
```

En el servidor Plesk, la estructura utilizada es:

```text
httpdocs/
└── biometria/
    ├── api.php
    ├── index.html
    ├── css/
    ├── js/
    └── server/
```

La configuración privada de conexión a MariaDB debe completarse únicamente en el servidor y no debe subirse al repositorio.

## Documentación

La carpeta `docs/` contiene el diseño y la documentación técnica del proyecto:

- `architecture.md`: arquitectura y flujo completo.
- `database.md`: diseño de la base de datos.
- `api.md`: diseño del API REST.
- `deployment-plesk.md`: despliegue del sistema.
- `class-audit.md`: correspondencia entre diseño y código.
- `validation.md`: comprobaciones y criterios de validación.

## Estado del Sprint 0

Comprobado de extremo a extremo:

```text
Medida ficticia en Arduino
        ↓
BLE / iBeacon
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

Resultado de prueba:

```text
O₃ = 123 ppb
Temperatura = -12 °C
```

La versión con adquisición física del sensor se conserva de forma independiente en la rama:

```text
sensor-real-final
```
