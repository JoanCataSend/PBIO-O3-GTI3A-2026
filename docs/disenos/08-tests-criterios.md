# 08 · Diseño de tests y criterios de aceptación

## Criterio de aceptación funcional

```text
Medida ficticia C++
    --> BLE
    --> Android
    --> API REST
    --> lógica de negocio
    --> MariaDB
    --> web
```

La medida debe conservar su valor de principio a fin.

## Tests de protocolo Android

### Major

```text
idMedida:N, contador:N --> codificarMajor() --> major:N
```

Caso:

```text
idMedida = 14
contador = 16
major esperado = 3600
```

### Minor O3

```text
[00, 7B] --> bytesToUnsignedInt() --> 123
```

### Minor temperatura

```text
[FF, F4] --> bytesToSignedInt16() --> -12
```

## Tests lógica de negocio

`LogicaUnitTest.php` verifica:

```text
MedidaEntrada válida                   --> aceptada
MedidaEntrada sin uuid                 --> Error
uuid vacío                             --> Error
valor no entero                        --> Error
contador = -1                          --> Error
contador = 256                         --> Error
contador = 0                           --> aceptado
contador = 255                         --> aceptado
MedidaVista con numéricos como Texto   --> normalizada a enteros
consultaMedidaVista                    --> une las tres tablas
```

Resultado validado:

```text
10/10
```

## Test de integración API

Cadena probada:

```text
cliente de test --> HTTPS --> api.php --> Logica.php --> MariaDB
```

Casos:

```text
health devuelve ok
base declarada = jcatsen_pbio
existe GTI Joan
existe O3 ID 14 / ppb
existe Temperatura ID 12
listar medidas devuelve colección
```

Resultado validado:

```text
6/6
```

## Test instrumentado Android

```text
aplicación instalada --> paquete = org.jordi.prueba2025
```

## Test manual final

La evidencia debe mostrar al menos:

1. Monitor Serie con `123` y `-12`.
2. Android recibiendo `123` y `-12` y mostrando `Servidor: medida guardada`.
3. Web mostrando `123` y `-12`.
4. Si es posible, tabla `Medida` de MariaDB con las filas correspondientes.
