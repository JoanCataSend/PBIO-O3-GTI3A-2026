# Validación

## Firmware

1. Compilar `firmware/NodoO3/NodoO3.ino`.
2. Subir a la SparkFun.
3. Verificar por Monitor Serie:
   - `Vgas`;
   - `Vref`;
   - temperatura;
   - O3 RAW;
   - O3 CORR;
   - O3 FILTRADO;
   - Major/Minor.

## API / BBDD

Abrir:

```text
http://pbio.jcatsen.upv.edu.es/api.php?accion=health
```

Esperado:

```json
{"ok":true,"database":"jcatsen_pbio"}
```

Después abrir:

```text
http://pbio.jcatsen.upv.edu.es/api.php?accion=tipos
```

y comprobar los IDs 11, 12, 13 y 14.

## Android

1. Instalar en teléfono físico.
2. Pulsar `Buscar GTI Joan`.
3. Confirmar O₃ y temperatura.
4. Confirmar `Servidor: medida guardada`.

## MariaDB

Abrir la tabla `Medida` en phpMyAdmin.

Deben aparecer filas nuevas de tipo:

```text
12 → Temperatura
14 → O3
```

## Web

Abrir:

```text
http://pbio.jcatsen.upv.edu.es/
```

Comprobar:

- tarjetas de O₃ y temperatura;
- RSSI;
- filtros;
- gráfica;
- historial;
- actualización automática.
