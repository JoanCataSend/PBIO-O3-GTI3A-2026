# 03 · Diseño de la lógica de negocio

La lógica del negocio es independiente de HTTP y de las vistas. Su interfaz lógica se implementa en `server/Logica.php`.

## Tipos

```text
MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)

MedidaVista = (
    medidaId:N,
    dispositivoId:N,
    uuid:Texto,
    dispositivo:Texto,
    tipoMedidaId:N,
    tipoMedida:Texto,
    unidad:Texto,
    valor:Z,
    contador:N,
    rssi:Z,
    fechaHora:Texto
)

Dispositivo = (dispositivoId:N, uuid:Texto, nombre:Texto)
TipoMedida  = (tipoMedidaId:N, nombre:Texto, unidad:Texto)
EstadoBD    = (ok:VoF, database:Texto)
```

## Interfaz lógica

```text
conexionBD() --> ConexionBD | Error
probarConexion() --> EstadoBD | Error

datos:MedidaEntrada
    --> insertarMedida()
    --> MedidaVista | Error

medidaId:N
    --> buscarMedidaConId()
    --> MedidaVista | Error

filtros:FiltrosMedida
    --> listarMedidas()
    --> [MedidaVista] | Error

listarDispositivos()
    --> [Dispositivo] | Error

listarTiposMedida()
    --> [TipoMedida] | Error

dispositivoId:N, tipoMedidaId:N
    --> buscarUltimaMedida()
    --> MedidaVista | Error

datos:MedidaEntrada
    --> validarMedidaEntrada()
    --> | Error

consultaMedidaVista()
    --> consulta:Texto

fila:MedidaVistaBD
    --> normalizarMedidaVista()
    --> MedidaVista
```

## Precondiciones de `MedidaEntrada`

```text
uuid != ""
tipoMedidaId ∈ Z
valor ∈ Z
contador ∈ Z
rssi ∈ Z
0 <= contador <= 255
```

La existencia del UUID y del tipo se comprueba antes de insertar.

## Algoritmo `insertarMedida()`

```text
validarMedidaEntrada(datos)

buscar dispositivo cuyo uuid = datos.uuid
si no existe
    Error NO_ENCONTRADO

buscar tipo cuyo tipoMedidaId = datos.tipoMedidaId
si no existe
    Error NO_ENCONTRADO

insertar Medida(
    dispositivoId,
    tipoMedidaId,
    valor,
    contador,
    rssi
)

medidaId <- identificador generado
resultado <- buscarMedidaConId(medidaId)
devolver resultado
```

## Algoritmo `listarMedidas()`

```text
condiciones <- []

si dispositivoId no está vacío
    añadir condición por dispositivo

si tipoMedidaId no está vacío
    añadir condición por tipo

si desde no está vacío
    añadir fechaHora >= desde

si hasta no está vacío
    añadir fechaHora <= hasta

consultar MedidaVista aplicando condiciones
ordenar por fechaHora descendente y medidaId descendente
limitar a 500
normalizar cada fila
```

## Algoritmo `validarMedidaEntrada()`

```text
camposObligatorios <- [uuid, tipoMedidaId, valor, contador, rssi]

∀ campo ∈ camposObligatorios
    si campo no existe
        Error DATO_INVALIDO

si uuid no es Texto o está vacío
    Error DATO_INVALIDO

∀ campo ∈ [tipoMedidaId, valor, contador, rssi]
    si campo no representa Z
        Error DATO_INVALIDO

si contador < 0 o contador > 255
    Error DATO_INVALIDO
```

## Segregación de responsabilidades

`Logica.php` conoce SQL y reglas de datos, pero no:

- códigos HTTP;
- widgets Android;
- DOM del navegador;
- BLE;
- representación visual.
