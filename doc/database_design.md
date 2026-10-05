# database_design.md

## Component Design (Diseño del Componente)

**Componente:** `database`
**Implementación canónica para revisión:** `src/database/`
**Código operativo equivalente:** `server/database/`

El almacenamiento relacional usa MariaDB/InnoDB y separa dispositivos, tipos de medida y observaciones. El esquema evita duplicar nombre/unidad en cada fila de medida y preserva integridad referencial.

### Esquema relacional

```text
Dispositivo(
    dispositivoId:N PK AUTOINCREMENT,
    uuid:Texto UNIQUE NOT NULL,
    nombre:Texto NOT NULL
)

TipoMedida(
    tipoMedidaId:N PK,
    nombre:Texto UNIQUE NOT NULL,
    unidad:Texto NOT NULL
)

Medida(
    medidaId:N PK AUTOINCREMENT,
    dispositivoId:N FK -> Dispositivo.dispositivoId,
    tipoMedidaId:N FK -> TipoMedida.tipoMedidaId,
    valor:Z NOT NULL,
    contador:N NOT NULL CHECK 0..255,
    rssi:Z NOT NULL,
    fechaHora:Texto NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
)
```

### Cardinalidades

```text
Dispositivo 1 ---- N Medida
TipoMedida  1 ---- N Medida
```

### Restricciones e índices

```text
UNIQUE Dispositivo.uuid
UNIQUE TipoMedida.nombre
FK Medida.dispositivoId -> Dispositivo.dispositivoId ON DELETE RESTRICT
FK Medida.tipoMedidaId -> TipoMedida.tipoMedidaId ON DELETE RESTRICT
CHECK 0 <= contador <= 255
INDEX Medida(dispositivoId, fechaHora)
INDEX Medida(tipoMedidaId, fechaHora)
```

### Normalización y dependencias

El esquema está normalizado para separar entidades con identidad propia: `Dispositivo` determina `uuid,nombre`; `TipoMedida` determina `nombre,unidad`; y `Medida` referencia ambas mediante claves foráneas y contiene únicamente los datos propios de la observación (`valor`, `contador`, `rssi`, `fechaHora`). Esto evita repetir metadatos de dispositivo o unidad en cada medida y mantiene una única fuente de verdad para los catálogos.

### Datos semilla de Sprint 0

```text
Dispositivo: EPSG-GTI-PROY-3A / GTI Joan
11 / CO2 / ppm
12 / Temperatura / °C
13 / Ruido / dB
14 / O3 / ppb
```

`schema.sql` crea el esquema, `seed.sql` inserta catálogos reproducibles y `drop.sql` elimina las tablas en orden seguro para recreación.

## Design Clarifications (Aclaraciones del Diseño)

- El campo `valor` es entero porque el protocolo BLE de Sprint 0 transporta un `Minor` de 16 bits y las unidades elegidas son discretas.
- `contador` es `TINYINT UNSIGNED` porque el protocolo reserva exactamente 8 bits para él.
- `fechaHora` se genera en el servidor de base de datos para mantener una referencia temporal centralizada.
- La base de datos no almacena el UUID ni la unidad repetidos en `Medida`; se obtienen mediante `JOIN`.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** SQL compatible con MariaDB/InnoDB.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** este componente no define funciones o métodos de aplicación; los scripts SQL incluyen cabecera de archivo y comentarios de propósito. La regla de bloques `--------------------` se aplica a las funciones/métodos de los componentes que sí los contienen.
- **Code Readability / Legibilidad del Código:** nombres de tablas/campos semánticos, restricciones explícitas, claves foráneas nombradas e índices alineados con los filtros principales.
- **Automated Testing / Pruebas Automatizadas:** `src/database/tests/schema_contract_test.py` verifica automáticamente tablas, claves, restricciones, índices y valores semilla; la integración real con MariaDB se comprueba además desde `ApiIntegracionTest.php`.
- **Source correspondence / Correspondencia:** `src/database/` reproduce los scripts de `server/database/` sin introducir una segunda versión del esquema.
