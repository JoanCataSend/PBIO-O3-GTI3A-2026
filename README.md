# PBIO · Sprint 0 · Joan Catala Sendra

Sprint 0 individual de **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**. El objetivo es demostrar una arquitectura completa en la que una medida ficticia generada por un nodo nRF52840 viaja por BLE/iBeacon a Android, se almacena mediante una API REST y lógica de negocio en MariaDB, y finalmente se consulta desde una página web.

## Criterio de aceptación

La demostración debe conservar el mismo valor de principio a fin:

```text
Firmware nRF52840
   ↓ iBeacon
Android (Java)
   ↓ HTTPS / JSON
API REST (PHP)
   ↓
Lógica de negocio (PHP)
   ↓
MariaDB
   ↑
Web (HTML/CSS/JS)
```

El firmware de Sprint 0 usa por defecto:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

Para la defensa, puede modificarse uno de esos valores en `src/firmware/NodoO3/Medidor.h`; el valor nuevo debe verse en Android y después en la web tras ser almacenado por el backend.

## Estructura del repositorio

```text
.
├── README.md
├── author.md
├── .gitignore
├── doc/
│   ├── firmware_design.md
│   ├── android_design.md
│   ├── database_design.md
│   ├── business_logic_design.md
│   ├── api_rest_design.md
│   ├── web_design.md
│   ├── prompts/              # exactamente los 6 prompts de la segunda tarea
│   ├── evidencias/
│   └── acceptance_test.md
├── src/
│   ├── firmware/
│   ├── android/
│   ├── database/
│   ├── business_logic/
│   ├── api_rest/
│   └── web/
└── scripts/
    ├── audit-repository.py
    └── test-sprint0.ps1
```

`src/` es la única implementación. Cada `doc/xxx_design.md` tiene exactamente un `src/xxx/` correspondiente, evitando copias duplicadas y ambigüedad para el profesor o el agente.

## Arquitectura y responsabilidades

- **firmware**: `Medidor` produce la medida ficticia; `Publicador` traduce tipo/contador/valor a Major/Minor; `EmisoraBLE` encapsula Bluefruit.
- **android**: `MainActivity` coordina BLE/UI; `TramaIBeacon` analiza el anuncio; `Utilidades` convierte bytes; `LogicaFake` representa la interfaz remota de negocio; `PeticionarioREST` encapsula HTTP.
- **database**: tres tablas normalizadas: `Dispositivo`, `TipoMedida` y `Medida`.
- **business_logic**: única capa que valida dominio y accede a MariaDB. No conoce HTTP ni HTML.
- **api_rest**: adaptador HTTP/JSON; traduce peticiones y excepciones, sin contener SQL ni reglas de negocio.
- **web**: `LogicaFake.js` es la única capa que usa `fetch`; `app.js` controla presentación y UX.

Los diseños formales están en `doc/` y usan la notación oficial de la asignatura.

## Protocolo BLE del Sprint 0

```text
Nombre BLE: GTI Joan
UUID iBeacon: EPSG-GTI-PROY-3A   (16 bytes ASCII)
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

La URL configurada para el Sprint 0 es:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

La recepción BLE debe validarse con un dispositivo físico compatible con BLE. Un emulador puede no entregar anuncios Bluetooth reales.

## Base de datos

Motor: MariaDB/InnoDB. Esquema: `src/database/schema.sql`.

El modelo contiene únicamente tres tablas porque es el mínimo normalizado que evita repetir metadatos en cada medida:

```text
Dispositivo 1 ─── N Medida N ─── 1 TipoMedida
```

La semilla `src/database/seed.sql` registra solo el dispositivo del Sprint 0 y los tipos realmente emitidos: Temperatura (12) y O3 (14).

Inicialización típica en MariaDB:

```bash
mysql -u USUARIO -p NOMBRE_BD < src/database/schema.sql
mysql -u USUARIO -p NOMBRE_BD < src/database/seed.sql
```

`drop.sql` permite eliminar las tablas en orden seguro durante una reinstalación controlada.

## Backend y credenciales

La configuración privada **no se versiona**. Para ejecutar el backend, copie:

```text
src/business_logic/SDBaseDatos.example.php
```

como:

```text
src/business_logic/SDBaseDatos.php
```

y configure host, base, usuario y contraseña. El archivo real está excluido por `.gitignore`.

En el repositorio, `api.php` puede cargar `../business_logic/Logica.php`. Para un despliegue Plesk sencillo, la estructura pública recomendada es:

```text
biometria/
├── index.html
├── css/
├── js/
├── api.php
└── server/
    ├── Logica.php
    └── SDBaseDatos.php   # privado; nunca subir a Git
```

Los scripts SQL no necesitan quedar dentro del directorio público una vez inicializada la base.

## Tests automáticos

Desde la raíz pueden ejecutarse por separado:

```bash
python scripts/audit-repository.py
python src/firmware/tests/firmware_contract_test.py
python src/database/tests/schema_contract_test.py
php src/business_logic/tests/LogicaUnitTest.php
node src/web/tests/web_unit_test.js
```

Android:

```text
cd src/android
# Windows
gradlew.bat test
# Linux/macOS
./gradlew test
```

Test instrumentado con teléfono/emulador conectado:

```text
gradlew.bat connectedAndroidTest
```

Integración contra un servidor desplegado:

```bash
php src/api_rest/tests/ApiIntegracionTest.php
```

Puede sobrescribirse la URL mediante `PBIO_API_URL`. Este test necesita red y un backend operativo; por ello se mantiene separado de los tests puramente locales.

En Windows, `scripts/test-sprint0.ps1` agrupa los tests locales y permite activar opcionalmente la integración remota.

## Demostración presencial

La secuencia recomendada está detallada en `doc/acceptance_test.md`. En resumen:

1. cambiar el valor ficticio de O3 o temperatura en `Medidor.h`;
2. compilar y cargar el firmware;
3. abrir Monitor Serie y comprobar el valor;
4. abrir la app Android en un teléfono físico, iniciar búsqueda y comprobar recepción + “Servidor: medida guardada”;
5. abrir la web, actualizar y comprobar que aparece el mismo valor;
6. si se solicita, mostrar la fila correspondiente en MariaDB/API.

## Git y entrega

La rúbrica exige un repositorio Git con ramas **`develop` y `master`** y commits periódicos. Esa historia no debe fabricarse dentro de un ZIP: debe conservarse en el repositorio real de GitHub. Antes de entregar, verificar que ambas ramas existan y que la versión final corregida esté committeada.

## Uso de IA y trazabilidad

`doc/prompts/` contiene exactamente los seis prompts solicitados: base de datos, lógica de negocio, API REST, lógica fake Android, lógica fake navegador y UX navegador. Cada prompt parte del diseño previo, exige cabeceras en la notación oficial y solicita tests automáticos. La implementación final debe revisarse siempre contra `doc/*_design.md`; los prompts no sustituyen el criterio de diseño.

## Evidencias

`doc/evidencias/` contiene capturas de una ejecución previa con los valores ficticios del Sprint 0. Son apoyo documental, no sustituyen el test presencial exigido por la rúbrica.
