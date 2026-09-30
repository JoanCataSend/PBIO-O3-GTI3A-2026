# PBIO · GTI Joan

Sistema completo del proyecto PBIO:

```text
Sensor O₃
↓
SparkFun nRF52840
↓ BLE / iBeacon
Android
↓ HTTP / JSON
PHP
↓
MariaDB
↑
Página web
```

## Estructura

```text
.
├── firmware/
│   └── NodoO3/
├── android/
├── server/
│   ├── Logica.php
│   ├── SDBaseDatos.php
│   ├── SDBaseDatos.example.php
│   └── database/
├── web/
│   ├── api.php
│   ├── index.html
│   ├── css/
│   └── js/
├── docs/
└── README.md
```

## 1. Base de datos

La BBDD de producción es:

```text
jcatsen_pbio
```

en MariaDB/Plesk.

Las tablas necesarias son:

```text
Dispositivo
TipoMedida
Medida
```

Los scripts están en:

```text
server/database/
```

Si las tablas y datos iniciales ya aparecen en phpMyAdmin, **no hace falta
volver a ejecutarlos**.

## 2. Configurar la contraseña del servidor

Editar:

```text
server/SDBaseDatos.php
```

y sustituir solamente:

```text
PON_AQUI_LA_CONTRASENA
```

por la contraseña real del usuario:

```text
jcatsen_pbio_user
```

`SDBaseDatos.php` está ignorado por Git.

## 3. Subir a Plesk

### Copiar la carpeta

```text
server/
```

a:

```text
/pbio.jcatsen.upv.edu.es/server/
```

### Copiar el CONTENIDO de

```text
web/
```

directamente a:

```text
/pbio.jcatsen.upv.edu.es/
```

La carpeta `public/` que creamos para Node.js ya no hace falta. Puedes dejarla vacía o borrarla. No hace falta activar Node.js ni usar npm.

## 4. Probar servidor

Abrir:

```text
http://pbio.jcatsen.upv.edu.es/api.php?accion=health
```

Debe aparecer:

```json
{
  "ok": true,
  "database": "jcatsen_pbio"
}
```

Después:

```text
http://pbio.jcatsen.upv.edu.es/api.php?accion=tipos
```

Debe devolver CO2, Temperatura, Ruido y O3.

## 5. Probar página web

Abrir:

```text
http://pbio.jcatsen.upv.edu.es/
```

Al principio puede no haber medidas.

## 6. Android

Abrir:

```text
android/
```

en Android Studio.

La aplicación:

1. busca `GTI Joan`;
2. valida `EPSG-GTI-PROY-3A`;
3. muestra O₃ y temperatura;
4. envía cada medida nueva a:

```text
http://pbio.jcatsen.upv.edu.es/api.php
```

## 7. Tests Android

Dentro de `android/`:

```text
gradlew.bat test
```

En Linux/macOS:

```text
./gradlew test
```

## 8. Firmware

Abrir:

```text
firmware/NodoO3/NodoO3.ino
```

en Arduino IDE.

Compilar y subir a la SparkFun.

## 9. Prueba completa

Con todo activo:

```text
SparkFun
↓ BLE
Android
↓ POST
api.php
↓
MariaDB
↓ GET
web
```

En Android debe aparecer:

```text
Servidor: medida guardada
```

En phpMyAdmin deben aparecer filas nuevas en `Medida`.

Y la web debe actualizar el histórico automáticamente cada 5 segundos.

## Documentación

- `docs/architecture.md`
- `docs/database.md`
- `docs/api.md`
- `docs/deployment-plesk.md`
- `docs/class-audit.md`
- `docs/validation.md`
