# Prompt 2 - Lógica de negocio backend

```text
Implementa `src/business_logic/Logica.php` en PHP 8+ a partir de `doc/business_logic_design.md` y `doc/database_design.md`.

REQUISITO DE ARQUITECTURA
La lógica de negocio debe depender solo de tipos de dominio y de persistencia. No debe importar, invocar ni referenciar el componente `communication`, sus rutas o clases de protocolo.

INTERFAZ LÓGICA
probarConexion() --> estado: EstadoBD
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
medida_id: N --> buscarMedidaConId() --> medida: MedidaVista
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]
dispositivo_id: N, tipo_medida_id: N --> buscarUltimaMedida() --> medida: MedidaVista
datos: MedidaEntrada --> validarMedidaEntrada()
conexionBD() --> conexion: ConexionBD
consultaMedidaVista() --> consulta: Text
fila: MedidaVista --> normalizarMedidaVista() --> medida: MedidaVista

ALINEACIÓN CON BBDD
Las consultas deben usar exactamente Dispositivo, TipoMedida y Medida con las columnas descritas en `database_design.md`. Usa sentencias preparadas para valores variables.

VALIDACIÓN
- uuid exactamente 16 caracteres.
- tipoMedidaId y contador: 0..255.
- O3 (14): 0..65535.
- Temperatura (12): -32768..32767.
- rssi entero.
- comprobar existencia de dispositivo y tipo antes de insertar.
- listados limitados a 500 filas.

CONFIGURACIÓN
`SDBaseDatos.php` es privado y no se versiona; entrega `SDBaseDatos.example.php` sin secretos.

COMENTARIOS
Cada función debe tener un bloque `--------------------` con diseño lógico usando solo N, Z, R, B, Text, agregaciones y colecciones, más una breve descripción.

TESTS
Genera `src/business_logic/tests/LogicaUnitTest.php` sin necesitar una BBDD real para las comprobaciones de validación y contrato estático. Cubre límites, normalización y que la consulta de vista relacione las tres tablas.

No rediseñes la capa.
```
