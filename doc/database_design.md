# database_design.md

## Diseño del Componente

**Componente:** `database`  
**Implementación:** `src/database/`  
**Motor:** MariaDB / InnoDB.

El diseño sigue literalmente el formato exigido por `Database_Design_Spec.md`: `TABLE`, `DESCRIPTION`, `COLUMNS`, `PRIMARY KEY`, `FOREIGN KEYS` y `CONSTRAINTS`.

```text
====================================================================================
TABLE: Dispositivo

DESCRIPTION: Stores each physical PBIO node known by the system.

COLUMNS:

+ dispositivoId | INT UNSIGNED | NOT NULL | AUTO_INCREMENT
+ uuid | CHAR(16) | NOT NULL | NO DEFAULT
+ nombre | VARCHAR(100) | NOT NULL | NO DEFAULT

PRIMARY KEY: dispositivoId

FOREIGN KEYS:

+ None

CONSTRAINTS:

+ UNIQUE (uuid)
+ CHECK (CHAR_LENGTH(uuid) = 16)
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

+ medidaId | BIGINT UNSIGNED | NOT NULL | AUTO_INCREMENT
+ dispositivoId | INT UNSIGNED | NOT NULL | NO DEFAULT
+ tipoMedidaId | TINYINT UNSIGNED | NOT NULL | NO DEFAULT
+ valor | INT | NOT NULL | NO DEFAULT
+ contador | TINYINT UNSIGNED | NOT NULL | NO DEFAULT
+ rssi | SMALLINT | NOT NULL | NO DEFAULT
+ fechaHora | DATETIME(3) | NOT NULL | CURRENT_TIMESTAMP(3)

PRIMARY KEY: medidaId

FOREIGN KEYS:

+ dispositivoId -> Dispositivo(dispositivoId)
+ tipoMedidaId -> TipoMedida(tipoMedidaId)

CONSTRAINTS:

+ CHECK (contador BETWEEN 0 AND 255)
+ CHECK (valor BETWEEN -32768 AND 65535)
+ ON UPDATE CASCADE / ON DELETE RESTRICT for both foreign keys
+ INDEX (dispositivoId, tipoMedidaId, fechaHora, medidaId)
+ INDEX (tipoMedidaId, fechaHora, medidaId)
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
    uuid = "EPSG-GTI-PROY-3A"
    nombre = "GTI Joan"

TipoMedida:
    12 / "Temperatura" / "°C"
    14 / "O3" / "ppb"
```

## Aclaraciones del Diseño

- El esquema SQL conserva `camelCase` porque esos son los identificadores físicos de las columnas implementadas; la lógica abstracta usa nombres de variable en minúsculas con guion bajo.
- `Dispositivo`, `TipoMedida` y `Medida` son las únicas tablas necesarias para el Sprint 0; separar unidades en una cuarta tabla no aporta información adicional.
- `rssi` se guarda como metadato de recepción de cada medida.
- `fechaHora` la genera MariaDB, evitando depender del reloj del teléfono.
- El contador no es único: usa 8 bits y puede repetirse después de desbordar.

## Reglas Generales

- **Lenguaje de Programación:** SQL compatible con MariaDB/InnoDB.
- **Encabezados de Funciones/Métodos:** este componente no contiene funciones de aplicación; cada script SQL debe mantener cabecera de archivo con nombre, descripción, copyright, fecha, autor y aportación.
- **Legibilidad del Código:** tablas, claves, `CHECK`, claves foráneas e índices deben corresponder exactamente con este diseño.
- **Pruebas Automatizadas:** `src/database/tests/schema_contract_test.py` verifica tablas, columnas, restricciones, índices y semilla mínima.
