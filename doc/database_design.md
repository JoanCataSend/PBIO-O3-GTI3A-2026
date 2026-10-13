# database_design.md

## Diseño del Componente

**Componente:** `database`  
**Implementación:** `src/database/`  
**Motor:** MariaDB / InnoDB.

El diseño sigue el formato exigido por `Database_Design_Spec.md`: `TABLE`, `DESCRIPTION`, `COLUMNS`, `PRIMARY KEY`, `FOREIGN KEYS` y `CONSTRAINTS`.

```text
====================================================================================
TABLE: Dispositivo

DESCRIPTION: Stores each physical PBIO node known by the system.

COLUMNS:

+ dispositivoId | CHAR(16) | NOT NULL | NO DEFAULT
+ nombre | VARCHAR(100) | NOT NULL | NO DEFAULT

PRIMARY KEY: dispositivoId

FOREIGN KEYS:

+ None

CONSTRAINTS:

+ CHECK (CHAR_LENGTH(dispositivoId) = 16)
====================================================================================

====================================================================================
TABLE: TipoMedida

DESCRIPTION: Stores the catalogue of measurement types emitted by the node.

COLUMNS:

+ tipoMedidaId | TINYINT UNSIGNED | NOT NULL | NO DEFAULT
+ nombre | VARCHAR(50) | NOT NULL | NO DEFAULT
+ unidad | VARCHAR(20) | NOT NULL | NO DEFAULT

PRIMARY KEY: tipoMedidaId

FOREIGN KEYS:

+ None

CONSTRAINTS:

+ UNIQUE (nombre)
====================================================================================

====================================================================================
TABLE: Medida

DESCRIPTION: Stores each measurement received from a registered device.

COLUMNS:

+ dispositivoId | CHAR(16) | NOT NULL | NO DEFAULT
+ tipoMedidaId | TINYINT UNSIGNED | NOT NULL | NO DEFAULT
+ valor | INT | NOT NULL | NO DEFAULT
+ fechaHora | DATETIME(3) | NOT NULL | CURRENT_TIMESTAMP(3)

PRIMARY KEY: (dispositivoId, fechaHora)

FOREIGN KEYS:

+ dispositivoId -> Dispositivo(dispositivoId)
+ tipoMedidaId -> TipoMedida(tipoMedidaId)

CONSTRAINTS:

+ CHECK (valor BETWEEN -32768 AND 65535)
+ ON UPDATE CASCADE / ON DELETE RESTRICT for both foreign keys
====================================================================================
```

### Relaciones

```text
Dispositivo 1 ---- N Medida
TipoMedida  1 ---- N Medida
```

### Datos iniciales del Sprint 0

```text
Dispositivo:
    dispositivoId = "EPSG-GTI-PROY-3A"
    nombre = "GTI Joan"

TipoMedida:
    12 / "Temperatura" / "°C"
    14 / "O3" / "ppb"
```

## Aclaraciones del Diseño

- La persistencia se ha simplificado específicamente para Sprint 0 siguiendo la revisión docente.
- `dispositivoId` reutiliza directamente el identificador textual de 16 caracteres del nodo; por eso no existe una segunda columna `uuid`.
- `Medida` conserva únicamente los datos necesarios para persistir una observación: dispositivo, tipo, valor y fecha/hora.
- `contador` y `rssi` siguen siendo útiles en BLE/Android, pero no se persisten porque no son necesarios para el objetivo del Sprint 0.
- `medidaId` se elimina: una fila queda identificada por la clave compuesta `(dispositivoId, fechaHora)`.
- `fechaHora` la genera MariaDB, evitando depender del reloj del teléfono.

## Reglas Generales

- **Lenguaje de Programación:** SQL compatible con MariaDB/InnoDB.
- **Encabezados de Funciones/Métodos:** este componente no contiene funciones de aplicación; cada script SQL debe mantener cabecera de archivo con nombre, descripción, copyright, fecha, autor y aportación.
- **Legibilidad del Código:** tablas, claves y restricciones deben corresponder exactamente con este diseño simplificado.
- **Pruebas Automatizadas:** `src/database/tests/schema_contract_test.py` verifica tablas, columnas, claves y semilla mínima.
