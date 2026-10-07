# database_design.md

## Diseño del Componente

**Componente:** `database`  
**Implementación:** `src/database/`  
**Motor:** MariaDB / InnoDB.

Se usa un modelo relacional de **tres tablas**. Es el mínimo que evita duplicar metadatos de dispositivo y de tipo en cada medida y mantiene integridad referencial sin sobrediseñar el Sprint 0.

### Modelo relacional

```text
Dispositivo(
    dispositivo_id: N PK AUTOINCREMENT,
    uuid: Text UNIQUE NOT NULL,
    nombre: Text NOT NULL
)

TipoMedida(
    tipo_medida_id: N PK,
    nombre: Text UNIQUE NOT NULL,
    unidad: Text NOT NULL
)

Medida(
    medida_id: N PK AUTOINCREMENT,
    dispositivo_id: N FK -> Dispositivo.dispositivo_id,
    tipo_medida_id: N FK -> TipoMedida.tipo_medida_id,
    valor: Z NOT NULL,
    contador: N NOT NULL,
    rssi: Z NOT NULL,
    fecha_hora: Text NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
)
```

### Cardinalidades

```text
Dispositivo 1 ---- N Medida
TipoMedida  1 ---- N Medida
```

### Restricciones

```text
Dispositivo.uuid es único y tiene 16 caracteres
0 <= TipoMedida.tipo_medida_id <= 255
-32768 <= Medida.valor <= 65535
0 <= Medida.contador <= 255
FK Medida.dispositivo_id -> Dispositivo.dispositivo_id
FK Medida.tipo_medida_id -> TipoMedida.tipo_medida_id
```

### Índices

```text
UNIQUE Dispositivo(uuid)
UNIQUE TipoMedida(nombre)
INDEX Medida(dispositivo_id, tipo_medida_id, fecha_hora, medida_id)
INDEX Medida(tipo_medida_id, fecha_hora, medida_id)
```

El primer índice soporta la consulta de última medida y filtros por dispositivo; el segundo evita degradar las consultas por tipo cuando no se selecciona dispositivo.

### Datos iniciales del Sprint 0

```text
Dispositivo:
    "EPSG-GTI-PROY-3A" / "GTI Joan"

Tipos:
    12 / "Temperatura" / "°C"
    14 / "O3" / "ppb"
```

No se insertan CO2 ni Ruido porque el firmware final no los publica.

## Aclaraciones del Diseño

- La notación lógica usa `dispositivo_id`, `tipo_medida_id`, `medida_id` y `fecha_hora`; el esquema SQL implementado conserva los nombres `dispositivoId`, `tipoMedidaId`, `medidaId` y `fechaHora`. La correspondencia es uno-a-uno.
- No se usa una tabla adicional de unidades: `unidad` depende del tipo y separar esa única cadena añadiría complejidad sin beneficio.
- No se guarda `uuid`, nombre de dispositivo ni unidad repetidos dentro de `Medida`; se recuperan con `JOIN`.
- `rssi` se almacena como metadato de recepción porque lo produce Android y es útil para diagnóstico, pero no constituye un tipo de medida ambiental independiente.
- `fecha_hora` la crea MariaDB para tener una referencia temporal única del servidor.
- No se hace `UNIQUE` sobre contador porque el contador es de 8 bits y vuelve a cero; una combinación válida puede repetirse con el tiempo.

## Reglas Generales

- **Lenguaje de Programación:** SQL compatible con MariaDB/InnoDB.
- **Encabezados de Funciones/Métodos:** este componente no define funciones de aplicación; cada script SQL sí debe tener cabecera de archivo con nombre, descripción, copyright, fecha, autor y aportación.
- **Legibilidad del Código:** restricciones, claves foráneas e índices deben estar nombrados y reflejar el diseño anterior.
- **Pruebas Automatizadas:** `src/database/tests/schema_contract_test.py` comprueba estructura, restricciones, índices y semilla mínima; la integración real se comprueba mediante la API.
