# PBIO · Sprint 0 · Joan Catala Sendra

Sprint 0 individual de **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**. El objetivo es demostrar un recorrido extremo a extremo reproducible: una medida ficticia generada por el nodo nRF52840 se publica como iBeacon, Android la recibe, la lógica de negocio del cliente la envía al backend, la lógica de negocio del servidor la persiste en MariaDB y una GUI web vuelve a consultarla.

## Criterio de aceptación obligatorio

```text
Firmware nRF52840
   ↓ iBeacon
Android GUI/BLE
   ↓
frontend_business_logic (proxy Android)
   ↓ HTTPS / JSON
communication (PHP)
   ↓
business_logic (PHP)
   ↓
database (MariaDB)
   ↑
frontend_business_logic (proxy web)
   ↑
GUI web
```

El firmware usa por defecto:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

Para la defensa puede modificarse uno de esos valores en `src/firmware/NodoO3/Medidor.h`. El mismo valor debe verse en Android y después en la GUI web una vez almacenado.

## Arquitectura exigida por el agente revisor v2

La revisión v2 exige separar explícitamente el backend en la tríada:

```text
communication -> business_logic -> database
```

También exige que toda GUI invoque un componente separado de lógica de negocio del cliente. Por eso el repositorio incluye:

```text
frontend_business_logic
```

La interfaz pública de ese proxy utiliza **las mismas firmas lógicas** que el subconjunto correspondiente de `business_logic`. Los mecanismos HTTP, `fetch`, JSON, callbacks y URLs quedan encapsulados debajo de esa interfaz.

## Estructura del repositorio

```text
.
├── README.md
├── author.md
├── .gitignore
├── doc/
│   ├── firmware_design.md
│   ├── android_design.md
│   ├── communication_design.md
│   ├── business_logic_design.md
│   ├── database_design.md
│   ├── frontend_business_logic_design.md
│   ├── gui_design.md
│   ├── acceptance_test.md
│   ├── ai_traceability.md
│   ├── prompts/
│   └── evidencias/
├── src/
│   ├── firmware/
│   ├── android/
│   ├── communication/
│   ├── business_logic/
│   ├── database/
│   ├── frontend_business_logic/
│   └── gui/
└── scripts/
    ├── audit-repository.py
    ├── test-sprint0.ps1
    └── build-plesk-package.ps1
```

Cada `doc/xxx_design.md` corresponde a `src/xxx/`. No se mantienen copias paralelas de implementación dentro del repositorio.

## Responsabilidades

- **firmware:** genera O3/temperatura ficticios y los publica en iBeacon.
- **android:** GUI móvil, permisos, escaneo BLE, decodificación de iBeacon y coordinación de dominio.
- **frontend_business_logic:** proxy/fake consumido por Android y por la GUI web; encapsula comunicación remota.
- **communication:** punto de entrada HTTP/JSON; decodifica, despacha y traduce errores de protocolo.
- **business_logic:** validación de dominio, consultas y persistencia; no depende de la capa de comunicación.
- **database:** esquema relacional MariaDB con `Dispositivo`, `TipoMedida` y `Medida`.
- **gui:** presentación web; no contiene `fetch` ni rutas de servidor.

## Protocolo BLE

```text
Nombre BLE: GTI Joan
UUID iBeacon: EPSG-GTI-PROY-3A  (16 bytes ASCII)
Manufacturer ID: 0x004C
ID Temperatura: 12
ID O3: 14
Major = (id_medida << 8) OR contador
Minor = 16 bits del valor
```

El contador ocupa 8 bits. O3 se interpreta como natural de 16 bits y temperatura como entero con signo de 16 bits.

## Android

Proyecto Android Studio: `src/android/`.

```text
namespace/applicationId: es.upv.jcatsen.pbio
compileSdk: 33
minSdk: 28
targetSdk: 32
Android Gradle Plugin: 7.3.0
Gradle wrapper: 7.4
```

La URL de Sprint 0 se encuentra encapsulada en la implementación Android de `frontend_business_logic`:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

`src/android/app/build.gradle` añade los fuentes Java de `src/frontend_business_logic/android/` mediante `sourceSets`. Esto permite mantener los componentes separados en el repositorio sin duplicar clases y sin cambiar el package de la aplicación.

La recepción BLE debe validarse en un dispositivo físico compatible con BLE.

## Base de datos

Motor: MariaDB/InnoDB.

El diseño formal de tablas está en `doc/database_design.md` y sigue el formato `TABLE / DESCRIPTION / COLUMNS / PRIMARY KEY / FOREIGN KEYS / CONSTRAINTS` requerido por el agente v2.

Inicialización:

```bash
mysql -u USUARIO -p NOMBRE_BD < src/database/schema.sql
mysql -u USUARIO -p NOMBRE_BD < src/database/seed.sql
```

`drop.sql` elimina las tablas en orden seguro para una reinstalación controlada.

## Configuración privada del backend

Las credenciales nunca se versionan. Copie:

```text
src/business_logic/SDBaseDatos.example.php
```

como:

```text
src/business_logic/SDBaseDatos.php
```

para un entorno local, o configure el equivalente privado al desplegar.

`.gitignore` excluye el archivo real.

## Cómo desplegar en Plesk

La estructura del repositorio está optimizada para la auditoría arquitectónica y no duplica código. Para generar una carpeta de despliegue plana compatible con Plesk:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\scripts\build-plesk-package.ps1
```

Se genera:

```text
.dist/plesk/biometria/
├── index.html
├── css/
├── js/
│   ├── app.js
│   └── LogicaFake.js
├── api.php
└── server/
    ├── Logica.php
    └── SDBaseDatos.example.php
```

Antes de usarlo en el servidor debe crearse **solo en Plesk** `server/SDBaseDatos.php` con las credenciales reales. No se debe añadir ese archivo a Git.

Los SQL se importan desde `src/database/` y no necesitan permanecer en el directorio público.

## Tests automáticos

Desde la raíz:

```powershell
python scripts\audit-repository.py
python src\firmware\tests\firmware_contract_test.py
python src\database\tests\schema_contract_test.py
node src\frontend_business_logic\tests\web_proxy_test.js
node src\gui\tests\gui_unit_test.js
```

Con PHP disponible:

```powershell
php src\business_logic\tests\LogicaUnitTest.php
php src\communication\tests\ApiIntegracionTest.php
```

El test de `communication` ejecuta siempre un contrato **offline y reproducible**. Para añadir la integración contra un despliegue real:

```powershell
$env:PBIO_API_URL="https://jcatsen.upv.edu.es/biometria/api.php"
php src\communication\tests\ApiIntegracionTest.php
```

La escritura real continúa desactivada por defecto; solo se habilita con `PBIO_API_WRITE_TEST=1`.

Android:

```powershell
cd src\android
.\gradlew.bat test
cd ..\..
```

Test instrumentado:

```powershell
cd src\android
.\gradlew.bat connectedAndroidTest
```

También puede ejecutarse el lanzador conjunto:

```powershell
.\scripts\test-sprint0.ps1
```

## Criterios de aceptación por entregable

`doc/acceptance_test.md` contiene una matriz reproducible para firmware, Android, proxy de frontend, comunicación, lógica de negocio, base de datos y GUI. El test presencial extremo a extremo sigue siendo obligatorio.

## IA y trazabilidad

Los seis prompts de trabajo se mantienen en `doc/prompts/`. `doc/ai_traceability.md` relaciona cada prompt, diseño e implementación para demostrar el flujo diseño -> prompt -> código -> revisión.

## Buenas prácticas Git

Se mantienen las ramas `develop`, `master` y `main`. Los cambios funcionales deben integrarse mediante commits identificables y el árbol de trabajo debe quedar limpio antes de la entrega.

## Nota de seguridad

Nunca subir:

- `SDBaseDatos.php` real;
- contraseñas o tokens;
- `local.properties`;
- carpetas `build/` o `.gradle/`;
- artefactos `.dist/` generados para despliegue.
