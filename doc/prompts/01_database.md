# Prompt 1 - Base de datos

```text
Actúa como implementador SQL. El diseño ya está decidido: NO lo sustituyas por otro.

OBJETIVO
Genera los archivos de `src/database/` para MariaDB/InnoDB a partir de este diseño:

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

RESTRICCIONES
- uuid: exactamente 16 caracteres y único.
- tipo_medida_id: TINYINT UNSIGNED.
- valor: entre -32768 y 65535.
- contador: entre 0 y 255.
- FK con ON DELETE RESTRICT y ON UPDATE CASCADE.
- Índices: (dispositivo_id,tipo_medida_id,fecha_hora,medida_id) y (tipo_medida_id,fecha_hora,medida_id).
- No crear más tablas. No crear tabla de unidades.

SEMILLA
- Dispositivo: EPSG-GTI-PROY-3A / GTI Joan.
- Tipo 12: Temperatura / °C.
- Tipo 14: O3 / ppb.
- No insertar CO2 ni Ruido.

ARCHIVOS
- schema.sql
- seed.sql
- drop.sql
- tests/schema_contract_test.py

CABECERAS Y ESTILO
Cada script debe incluir nombre, descripción, copyright 2026 Joan Catala Sendra (uso académico PBIO - UPV), fecha, autor y aportación. El SQL debe ser legible y reproducible.

TEST AUTOMÁTICO
El test Python debe leer los scripts reales y fallar si faltan tablas, FKs, CHECKs, índices, UUID o los dos tipos semilla; también debe fallar si aparecen CO2/Ruido en la semilla.

Devuelve archivos completos, sin TODOs, credenciales ni rediseños.
```
