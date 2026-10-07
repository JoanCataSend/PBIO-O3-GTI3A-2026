# Prompt 2 - Lógica de negocio backend

```text
Implementa `src/business_logic/Logica.php` en PHP 8 respetando EXACTAMENTE el diseño previo. Esta capa NO conoce HTTP ni HTML.

TIPOS LÓGICOS
MedidaEntrada=(uuid: Text,tipo_medida_id: N,valor: Z,contador: N,rssi: Z)
EstadoBD=(ok: B,database: Text)
MedidaVista=(medida_id: N,dispositivo_id: N,uuid: Text,dispositivo: Text,tipo_medida_id: N,tipo_medida: Text,unidad: Text,valor: Z,contador: N,rssi: Z,fecha_hora: Text)

OPERACIONES
conexionBD() --> conexion: ConexionBD
probarConexion() --> estado: EstadoBD
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
medida_id: N --> buscarMedidaConId() --> medida: MedidaVista
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]
dispositivo_id: N,tipo_medida_id: N --> buscarUltimaMedida() --> medida: MedidaVista
datos: MedidaEntrada --> validarMedidaEntrada() -->
consultaMedidaVista() --> consulta: Text
fila: MedidaVistaBD --> normalizarMedidaVista() --> medida: MedidaVista

REGLAS
- `declare(strict_types=1)` y mysqli.
- Credenciales en `SDBaseDatos.php`, nunca versionadas; entregar `SDBaseDatos.example.php` sin secretos.
- Sentencias preparadas para valores variables.
- uuid debe tener 16 caracteres.
- tipoMedidaId en 0..255, contador en 0..255, valor en -32768..65535, rssi entero.
- Comprobar que dispositivo y tipo existan antes de insertar.
- Limitar listados a 500 filas y validar filtros.
- Normalizar a enteros los campos numéricos devueltos por MariaDB.
- No generar códigos HTTP.

COMENTARIOS
Cada archivo: nombre, descripción, copyright, fecha, autor Joan Catala Sendra y aportación.
Cada función: bloque `--------------------`, firma en notación oficial (N,Z,R,B,Text, colecciones y tipos compuestos) y breve descripción.

TESTS
Genera `tests/LogicaUnitTest.php` sin necesitar BD real. Debe probar: entrada válida, campo ausente, uuid vacío/corto, valor no entero, límites de contador y valor, normalización de MedidaVista y que la consulta base haga JOIN de las tres tablas.

No cambies el diseño ni mezcles REST con la lógica.
```
