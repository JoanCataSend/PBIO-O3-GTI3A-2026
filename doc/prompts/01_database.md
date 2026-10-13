# Prompt 1 - Base de datos

```text
Implementa `src/database/` para MariaDB/InnoDB a partir de `doc/database_design.md`. El diseño está cerrado: no añadas ni elimines tablas.

Debes conservar exactamente las tres tablas y su formato relacional simplificado:
- Dispositivo(dispositivoId, nombre)
- TipoMedida(tipoMedidaId, nombre, unidad)
- Medida(dispositivoId, tipoMedidaId, valor, fechaHora)

REGLAS CLAVE
- `Dispositivo.dispositivoId` es CHAR(16), reutiliza directamente el identificador textual del nodo y es la PRIMARY KEY.
- No existe una segunda columna `uuid` en Dispositivo.
- `Medida` no contiene `medidaId`, `contador` ni `rssi`.
- PRIMARY KEY de Medida: (dispositivoId, fechaHora).
- Mantener las dos FOREIGN KEYS hacia Dispositivo y TipoMedida.
- `fechaHora` usa CURRENT_TIMESTAMP(3).
- Mantener el CHECK de valor de 16 bits indicado en `database_design.md`.

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

El test Python debe verificar automáticamente las tres tablas, la clave compuesta de Medida, claves foráneas, semilla y ausencia de las columnas eliminadas.

No incluyas credenciales ni cambies el diseño.
```
