# Prompt 1 - Base de datos

```text
Implementa `src/database/` para MariaDB/InnoDB a partir de `doc/database_design.md`. El diseño está cerrado: no añadas ni elimines tablas.

Debes conservar exactamente las tres tablas y su formato relacional:
- Dispositivo(dispositivoId, uuid, nombre)
- TipoMedida(tipoMedidaId, nombre, unidad)
- Medida(medidaId, dispositivoId, tipoMedidaId, valor, contador, rssi, fechaHora)

Respeta literalmente PRIMARY KEY, FOREIGN KEYS y CONSTRAINTS de `database_design.md`, incluidos UUID de 16 caracteres, límites de contador/valor, ON UPDATE CASCADE, ON DELETE RESTRICT e índices.

Genera:
- `src/database/schema.sql`
- `src/database/seed.sql`
- `src/database/drop.sql`
- `src/database/tests/schema_contract_test.py`

Semilla mínima:
- EPSG-GTI-PROY-3A / GTI Joan
- 12 / Temperatura / °C
- 14 / O3 / ppb
No insertar tipos adicionales.

Cada archivo debe incluir cabecera con nombre, descripción, copyright, fecha, autor Joan Catala Sendra y aportación.

El test Python debe verificar de forma automática las tres tablas, columnas, claves, CHECK, índices y semilla. Debe fallar si aparece una cuarta tabla o un tipo no previsto.

No incluyas credenciales ni cambies el diseño.
```
