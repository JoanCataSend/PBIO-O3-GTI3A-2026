# Prompt 4 - Frontend business logic Android

```text
Implementa el subconjunto Android de `doc/frontend_business_logic_design.md` dentro de `src/frontend_business_logic/android/`.

NO escribas código de GUI/BLE dentro de este componente. `src/android/` ya existe y solo debe consumir la interfaz de dominio.

INTERFAZ LÓGICA - debe ser idéntica al backend
probarConexion() --> estado: EstadoBD
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista

TIPOS
MedidaEntrada=(uuid: Text,tipo_medida_id: N,valor: Z,contador: N,rssi: Z)
EstadoBD=(ok: B,database: Text)
MedidaVista=(dispositivo_id: Text,dispositivo: Text,tipo_medida_id: N,tipo_medida: Text,unidad: Text,valor: Z,fecha_hora: Text)

ACLARACIÓN
`contador` y `rssi` siguen formando parte de la medida recibida por Android y de MedidaEntrada, pero el backend no los devuelve en MedidaVista porque ya no se persisten en Sprint 0.

IMPLEMENTACIÓN
- `LogicaFake` expone solo operaciones de dominio y adapta los resultados a `EstadoBD`/`MedidaVista`.
- `PeticionarioREST` encapsula URL, JSON, GET/POST, timeouts y diagnóstico de red.
- La GUI Android no debe conocer `PeticionarioREST` ni `JSONObject`.
- Callbacks/hilos son detalles de implementación y se omiten de la firma lógica.

URL DEL SPRINT
https://jcatsen.upv.edu.es/biometria/api.php

ARCHIVOS DE DOMINIO/PROXY
- MedidaEntrada.java
- MedidaVista.java
- EstadoBD.java
- LogicaFake.java
- PeticionarioREST.java
- tests Java del proxy

Cada método propio debe tener bloque `--------------------`, firma lógica y descripción. No cambies UUID, IDs BLE ni package `es.upv.jcatsen.pbio`.
```
