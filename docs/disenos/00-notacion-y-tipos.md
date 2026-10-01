# 00 · Notación y tipos comunes

Este diseño utiliza la notación de la asignatura y abstrae los detalles de implementación. Se representan **entradas y salidas lógicas**, no punteros, callbacks, referencias, hilos o mecanismos concretos de transporte.

## Tipos simples

```text
N      número natural
Z      número entero
R      número real
VoF    booleano
Texto  cadena de caracteres
[T]    colección/lista/array de T
[T]_n  array de T de tamaño fijo n
```

## Tipos del proyecto

```text
UUIDProyecto = [N]_16
Bytes2       = [N]_2
JSON         = Texto

MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)

Dispositivo = (
    dispositivoId:N,
    uuid:Texto,
    nombre:Texto
)

TipoMedida = (
    tipoMedidaId:N,
    nombre:Texto,
    unidad:Texto
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

FiltrosMedida = (
    dispositivoId:N | VACIO,
    tipoMedidaId:N | VACIO,
    desde:Texto | VACIO,
    hasta:Texto | VACIO
)

EstadoBD = (
    ok:VoF,
    database:Texto
)

ResultadoBLE = (
    bytes:[N],
    rssi:Z
)

Error = {
    DATO_INVALIDO,
    NO_ENCONTRADO,
    ERROR_BD,
    ERROR_HTTP,
    ERROR_BLE
}
```

## Constantes del protocolo

```text
NOMBRE_NODO   = "GTI Joan"
UUID_PROYECTO = "EPSG-GTI-PROY-3A"

ID_CO2         = 11
ID_TEMPERATURA = 12
ID_RUIDO       = 13
ID_O3          = 14

O3_FAKE          = 123 ppb
TEMPERATURA_FAKE = -12 °C
```

## Precondiciones comunes

```text
0 <= contador <= 255
uuid != ""
tipoMedidaId ∈ N
valor ∈ Z
rssi ∈ Z
```

## Convención de funciones

```text
entrada:Tipo --> funcion() --> salida:Tipo
```

Cuando puede fallar:

```text
entrada:Tipo --> funcion() --> salida:Tipo | Error
```

Cuando no hay datos de entrada o salida se omite la flecha correspondiente.
