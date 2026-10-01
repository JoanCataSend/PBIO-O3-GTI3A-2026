# Despliegue en Plesk

URL pública:

```text
https://jcatsen.upv.edu.es/biometria/
```

API:

```text
https://jcatsen.upv.edu.es/biometria/api.php
```

## Estructura en Plesk

Dentro de `httpdocs` debe existir:

```text
httpdocs/
└── biometria/
    ├── index.html
    ├── api.php
    ├── css/
    │   └── styles.css
    ├── js/
    │   ├── LogicaFake.js
    │   └── app.js
    └── server/
        ├── Logica.php
        └── SDBaseDatos.php
```

## 1. Base de datos

Crear/importar en MariaDB:

```text
server/database/schema.sql
server/database/seed.sql
```

Base utilizada:

```text
jcatsen_pbio
```

## 2. Configuración privada

Partir de:

```text
server/SDBaseDatos.example.php
```

y crear en el servidor:

```text
server/SDBaseDatos.php
```

La contraseña real se introduce **solo en Plesk**. `SDBaseDatos.php` está ignorado por Git y no debe publicarse.

## 3. Backend

Copiar al servidor:

```text
server/Logica.php
server/SDBaseDatos.php
```

hacia:

```text
/httpdocs/biometria/server/
```

## 4. Web + API

Copiar el contenido de `web/` a:

```text
/httpdocs/biometria/
```

`api.php` acepta tanto la estructura de despliegue anterior como la estructura del repositorio mediante resolución de ruta de `Logica.php`.

## 5. Comprobación

Abrir:

```text
https://jcatsen.upv.edu.es/biometria/api.php?accion=health
```

Esperado:

```json
{"ok":true,"database":"jcatsen_pbio"}
```

Después abrir:

```text
https://jcatsen.upv.edu.es/biometria/
```

No se utiliza Node.js ni un subdominio adicional en esta versión.
