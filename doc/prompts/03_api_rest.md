# Prompt 3 - Componente de comunicación REST

```text
Implementa el componente `communication` definido en `doc/communication_design.md`.

ARCHIVOS
- `src/communication/api.php`
- `src/communication/tests/ApiIntegracionTest.php`

RESPONSABILIDAD
`communication` es un adaptador HTTP/JSON. Puede invocar `src/business_logic/Logica.php`, pero no contiene SQL ni reglas de dominio.

RUTAS
POST api.php -> insertarMedida(cuerpo) -> 201 MedidaVista
GET api.php?accion=medidas -> listarMedidas(filtros) -> 200 [MedidaVista]
GET api.php?accion=ultima&dispositivoId=<N>&tipoMedidaId=<N> -> 200 MedidaVista
GET api.php?accion=dispositivos -> 200 [Dispositivo]
GET api.php?accion=tipos -> 200 [TipoMedida]
GET api.php?accion=health -> probarConexion() -> 200 EstadoBD

FUNCIÓN PROPIA
codigo: N, datos: Text --> responder()

ERRORES DE COMUNICACIÓN
entrada inválida -> 400
entidad no encontrada -> 404
método no soportado -> 405
fallo no previsto -> 500 con mensaje genérico

DESPLIEGUE
En el repositorio la lógica está en `../business_logic/Logica.php`. El mismo `api.php` puede aceptar una copia de despliegue en `./server/Logica.php`, pero esa copia no debe existir versionada en `src/communication/`.

TEST
`ApiIntegracionTest.php` debe ejecutar por defecto un contrato offline reproducible. Si se define `PBIO_API_URL`, añade pruebas HTTP contra el despliegue real; el POST válido es opt-in mediante `PBIO_API_WRITE_TEST=1` para no contaminar la base de datos.

Cada función propia debe tener bloque `--------------------`, diseño lógico y descripción. Mantén compatibilidad con PHP 8.0+ y no incluyas credenciales.
```
