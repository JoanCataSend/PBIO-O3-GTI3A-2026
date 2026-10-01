# Prompt · Tests, README y documentación

```text
Para un proyecto PBIO Sprint 0 con firmware BLE, Android, PHP/MariaDB y web, genera documentación y tests reproducibles.

Criterio funcional: firmware genera O3=123 ppb y Temperatura=-12 °C; Android debe recibirlos, enviarlos por HTTPS a la API, MariaDB debe almacenarlos y la web debe mostrar los mismos valores.

Android tests JUnit:
- Major: ID14 + contador16 = 3600 y decodificación inversa.
- Minor FF F4 = -12 signed int16.
- Minor 00 7B = 123 unsigned.
- URL exacta https://jcatsen.upv.edu.es/biometria/api.php.
- ruta contiene jcatsen.upv.edu.es/biometria.
Instrumented test: package org.jordi.prueba2025.

PHP unit tests: entrada válida, falta uuid, uuid vacío, valor no entero, contador -1, contador 256, límites 0/255, normalización de tipos numéricos, consulta con joins de Medida/Dispositivo/TipoMedida.

PHP integración, solo lectura, usando HTTPS: health ok, database jcatsen_pbio, dispositivo EPSG-GTI-PROY-3A / GTI Joan, O3 ID14 ppb, Temperatura ID12, listado de medidas array.

README debe explicar arquitectura, ramas main/master/develop/sensor-real-final, estructura, despliegue Plesk en https://jcatsen.upv.edu.es/biometria/, ejecución tests, criterio de aceptación y secretos no versionados.

También crea checklist de rúbrica que indique evidencia exacta por archivo. No afirmes que una prueba se ejecutó si no hay evidencia. Mantén separados "test automático" y "test manual de extremo a extremo".
```
