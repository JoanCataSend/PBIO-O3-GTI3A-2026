# 02 · Diseño lógico de Android

## Arquitectura

```text
MainActivity
   |
   +--> TramaIBeacon
   +--> Utilidades
   +--> MedidaEntrada
   +--> LogicaFake
             |
             +--> PeticionarioREST
```

---

## Tipo MedidaEntrada

```text
MedidaEntrada = (
    uuid:Texto,
    tipoMedidaId:N,
    valor:Z,
    contador:N,
    rssi:Z
)
```

Clase:

```text
------------------------ MedidaEntrada ------------------------
|
| uuid:Texto
| tipoMedidaId:N
| valor:Z
| contador:N
| rssi:Z
|
|
uuid:Texto,
tipoMedidaId:N,
valor:Z,
contador:N,
rssi:Z -------------> MedidaEntrada() -->
|
|
| JSON              <-- toJson() <--
|
---------------------------------------------------------------
```

---

## Clase TramaIBeacon

Estado privado:

```text
losBytes:[N]
uuid:[N]_16
major:[N]_2
minor:[N]_2
txPower:Z
valida:VoF
```

Diseño:

```text
------------------------- TramaIBeacon -------------------------
|
| losBytes:[N]
| uuid:[N]_16
| major:[N]_2
| minor:[N]_2
| txPower:Z
| valida:VoF
|
|
bytes:[N] ---------> TramaIBeacon() -->
|
|                    analizar() -->
|
| valida:VoF       <-- esValida() <--
|
| uuid:[N]_16      <-- getUUID() <--
|
| major:[N]_2      <-- getMajor() <--
|
| minor:[N]_2      <-- getMinor() <--
|
| txPower:Z        <-- getTxPower() <--
|
| bytes:[N]        <-- getLosBytes() <--
|
----------------------------------------------------------------
```

### Algoritmo `analizar()`

```text
∀ i válido en losBytes
    si losBytes[i..i+3] = 4C 00 02 15
        uuid <- losBytes[i+4 .. i+19]
        major <- losBytes[i+20 .. i+21]
        minor <- losBytes[i+22 .. i+23]
        txPower <- losBytes[i+24]
        valida <- VERDADERO
        terminar

valida <- FALSO
```

---

## Utilidades

Funciones estáticas:

```text
bytes:[N]   --> bytesToString()      --> texto:Texto
bytes:[N]   --> bytesToHexString()   --> hexadecimal:Texto
bytes:[N]   --> bytesToUnsignedInt() --> valor:N
bytes:[N]_2 --> bytesToSignedInt16() --> valor:Z
```

---

## LogicaFake Android

```text
datos:MedidaEntrada --> insertarMedida() --> MedidaVista | Error
```

Responsabilidad lógica:

```text
MedidaEntrada
     ↓
serializar
     ↓
PeticionarioREST.postJson(URL_API, datos)
     ↓
MedidaVista | Error
```

`MainActivity` no necesita conocer cómo se implementa HTTP.

---

## PeticionarioREST

```text
url:Texto, datos:JSON, callback:Callback --> postJson() -->
entrada:FlujoTexto --> leerTexto() --> texto:Texto | Error
```

El callback es un mecanismo de implementación; lógicamente la operación equivale a:

```text
url:Texto, datos:JSON --> postJson() --> JSON | Error
```

---

## MainActivity

Estado lógico relevante:

```text
NOMBRE_NODO:Texto = "GTI Joan"
UUID_PROYECTO:Texto = "EPSG-GTI-PROY-3A"
ID_TEMPERATURA:N = 12
ID_O3:N = 14
ultimoContadorO3Enviado:Z
ultimoContadorTemperaturaEnviado:Z
```

Funciones principales:

```text
onCreate() -->
tengoPermisosBLE() --> concedidos:VoF
pedirPermisosSiHacenFalta() -->
comprobarBluetooth() -->
iniciarBusqueda() -->
detenerBusqueda() -->
resultado:ResultadoBLE --> procesarResultado() -->

uuid:Texto,
idMedida:N,
valor:Z,
contador:N,
rssi:Z --> enviarMedidaSiEsNueva() -->
```

### Algoritmo `procesarResultado()`

Entrada abstracta:

```text
resultado:ResultadoBLE
```

Algoritmo:

```text
si resultado no contiene bytes
    terminar

trama <- TramaIBeacon(resultado.bytes)

si trama no es válida
    terminar

uuid <- bytesToString(trama.uuid)

si uuid != UUID_PROYECTO
    terminar

major <- bytesToUnsignedInt(trama.major)
idMedida <- 8 bits altos de major
contador <- 8 bits bajos de major
minorUnsigned <- bytesToUnsignedInt(trama.minor)
minorSigned <- bytesToSignedInt16(trama.minor)
rssi <- resultado.rssi

si idMedida = ID_O3
    valor <- minorUnsigned
si no
    valor <- minorSigned

enviarMedidaSiEsNueva(uuid, idMedida, valor, contador, rssi)
actualizar interfaz
```

### Algoritmo `enviarMedidaSiEsNueva()`

```text
si idMedida != ID_O3 y idMedida != ID_TEMPERATURA
    terminar

si el contador ya fue enviado para ese tipo
    terminar

recordar contador

datos <- MedidaEntrada(uuid, idMedida, valor, contador, rssi)
insertarMedida(datos)

si error
    liberar contador para permitir reintento
```
