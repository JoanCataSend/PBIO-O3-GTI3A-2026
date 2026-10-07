# Prompt 3 - API REST

```text
Genera `src/api_rest/api.php` como adaptador REST/JSON en PHP 8. La lógica real YA existe en `business_logic`; no dupliques validación de dominio ni escribas SQL.

RUTAS
POST api.php -> insertarMedida(cuerpo) -> 201 MedidaVista
GET api.php?accion=medidas -> listarMedidas(filtros) -> 200 [MedidaVista]
GET api.php?accion=ultima&dispositivoId=<N>&tipoMedidaId=<N> -> 200 MedidaVista
GET api.php?accion=dispositivos -> 200 [Dispositivo]
GET api.php?accion=tipos -> 200 [TipoMedida]
GET api.php?accion=health -> 200 EstadoBD

ERRORES
InvalidArgumentException -> 400
DomainException -> 404
método HTTP distinto de GET/POST -> 405
Throwable no previsto -> 500 con mensaje genérico, sin detalles internos
acción desconocida -> 400

FUNCIÓN PROPIA
codigo: N,datos: Json --> responder() -->

DESPLIEGUE
En el repositorio `Logica.php` está en `../business_logic/Logica.php`; en Plesk estará en `./server/Logica.php`. El mismo api.php debe poder resolver ambos casos.

COMENTARIOS
Cabecera de archivo completa con Joan Catala Sendra. Cada función propia con bloque `--------------------`, diseño lógico oficial y descripción.

TEST AUTOMÁTICO
Genera `tests/ApiIntegracionTest.php` de solo lectura. Debe aceptar `PBIO_API_URL` y usar por defecto `https://jcatsen.upv.edu.es/biometria/api.php`. Comprobar: health, base jcatsen_pbio, dispositivo GTI Joan, O3 ID14/ppb, Temperatura ID12/°C y listado de medidas.

No abras MariaDB a Internet y no incluyas credenciales.
```
