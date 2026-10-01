# Prompt · Android Sprint 0

```text
Genera la parte Android Java de PBIO para package org.jordi.prueba2025. Usa compileSdk 33, minSdk 28, targetSdk 32, Android Gradle Plugin 7.3.0, Gradle 7.4, support appcompat 28.0.0 y JUnit 4.13.2.

Clases exactas: MainActivity, TramaIBeacon, Utilidades, MedidaEntrada, LogicaFake y PeticionarioREST.

Debe escanear BLE en teléfono físico buscando nombre exacto "GTI Joan". Analiza iBeacon buscando bytes Apple 4C 00 02 15, extrae UUID/Major/Minor/TxPower y acepta solo UUID ASCII "EPSG-GTI-PROY-3A".

Major: ID en 8 bits altos y contador en 8 bajos. IDs usados: Temperatura=12, O3=14. O3 se decodifica unsigned; Temperatura signed int16. Muestra ambos, contador, RSSI y trama.

Evita duplicados guardando el último contador enviado por O3 y temperatura. Si es nueva, crea MedidaEntrada(uuid,tipoMedidaId,valor,contador,rssi) y llama a LogicaFake.insertarMedida(). URL exacta: https://jcatsen.upv.edu.es/biometria/api.php . PeticionarioREST hace POST JSON en ExecutorService, timeouts 6000 ms y callback correcto/error.

Añade permisos BLE compatibles con Android 11 y 12+, INTERNET y feature bluetooth_le. UI ScrollView sencilla con botones Buscar GTI Joan / Detener búsqueda.

Tests: Major 14/16=3600; FF F4=-12; 00 7B=123; URL exacta; ruta /biometria; test instrumentado del package.

Cada fichero fuente debe tener cabecera académica y cada método diseño lógico + descripción. Omite View/callbacks/punteros de las firmas lógicas cuando sean detalles de implementación.

Entrega todos los archivos completos necesarios y no cambies nombres/URL/IDs.
```
