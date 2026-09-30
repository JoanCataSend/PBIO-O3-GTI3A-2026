# Despliegue en Plesk

El subdominio ya creado es:

```text
pbio.jcatsen.upv.edu.es
```

y su raíz es:

```text
/pbio.jcatsen.upv.edu.es
```

## Copiar archivos

### 1. Backend privado

Copia la carpeta del repositorio:

```text
server/
```

a:

```text
/pbio.jcatsen.upv.edu.es/server/
```

Antes de subirla, abre:

```text
server/SDBaseDatos.php
```

y sustituye:

```text
PON_AQUI_LA_CONTRASENA
```

por la contraseña real de `jcatsen_pbio_user`.

### 2. Página web + API

Copia **el contenido** de:

```text
web/
```

directamente a:

```text
/pbio.jcatsen.upv.edu.es/
```

Debe quedar:

```text
/pbio.jcatsen.upv.edu.es/
├── index.html
├── api.php
├── css/
│   └── styles.css
├── js/
│   ├── LogicaFake.js
│   └── app.js
└── server/
    ├── SDBaseDatos.php
    └── Logica.php
```

La carpeta `public/` creada anteriormente para Node.js ya no hace falta.
No hace falta activar Node.js. PHP 8 + MariaDB son suficientes.
