# Implementación

`src/` contiene una única implementación por componente lógico:

```text
communication -> business_logic -> database

android -> frontend_business_logic -> communication
gui     -> frontend_business_logic -> communication

firmware -> Android por BLE (frontera física)
```

La carpeta `frontend_business_logic/` contiene dos implementaciones del mismo contrato lógico: Java para Android y JavaScript para la GUI web. Ambas exponen únicamente el subconjunto de operaciones de `business_logic` que necesita cada cliente.

No introducir copias de `communication`, `business_logic` o del proxy dentro de las carpetas GUI. Los paquetes de despliegue se generan con scripts y se mantienen fuera de Git.
