# Prompt 4 - Lógica fake del teléfono

```text
Implementa únicamente la lógica fake de Android y su cliente HTTP dentro del proyecto Java `es.upv.jcatsen.pbio`. NO rediseñes MainActivity ni el protocolo BLE.

CONTRATO LÓGICO
MedidaEntrada=(uuid: Text,tipo_medida_id: N,valor: Z,contador: N,rssi: Z)
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista

La lógica fake debe tener constantes públicas:
URL_API = "https://jcatsen.upv.edu.es/biometria/api.php"
URL_HEALTH = URL_API + "?accion=health"

RESPONSABILIDADES
- `MedidaEntrada`: objeto inmutable y serialización JSON con claves uuid, tipoMedidaId, valor, contador, rssi.
- `LogicaFake`: recibe MedidaEntrada, la serializa y delega la comunicación; no conoce BLE ni UI. Debe poder comprobar el endpoint `health`.
- `PeticionarioREST`: GET/POST JSON en hilo de trabajo, timeouts explícitos, `Accept: application/json`, `Content-Length` fijo para POST, lectura UTF-8 y diagnóstico diferenciado de HTTP, DNS, timeout y TLS.
- Los callbacks/hilos son detalles de implementación y no aparecen como datos en la firma lógica.

ARCHIVOS
- MedidaEntrada.java
- LogicaFake.java
- PeticionarioREST.java
- tests/ServidorUnitTest.java (o equivalente dentro de app/src/test)

COMENTARIOS
Cada archivo debe tener cabecera completa con Joan Catala Sendra. Cada método debe tener `--------------------`, firma en la notación oficial y descripción breve.

TESTS
Comprobar automáticamente las URLs HTTPS exactas de escritura y `health`. No hacer llamadas reales de red en el test unitario.

No cambies IDs, UUID, package ni otras clases Android.
```
