# 01 · Diseño lógico del firmware

## Arquitectura

```text
NodoO3
  |
  +--> Medidor
  |
  +--> Publicador
          |
          +--> EmisoraBLE
```

`NodoO3.ino` coordina; `Medidor` produce datos; `Publicador` los traduce al protocolo; `EmisoraBLE` realiza el advertising.

---

## Clase Medidor

```text
---------------------- Medidor ----------------------
|
|
|                 Medidor() -->
|
|
|                 iniciarMedidor() -->
|
|
| valorO3:Z     <-- medirO3() <--
|
|
| temperatura:Z <-- medirTemperatura() <--
|
------------------------------------------------------
```

### Funciones

```text
medirO3() --> valorO3:Z
```

Postcondición Sprint 0:

```text
valorO3 = 123
```

```text
medirTemperatura() --> temperatura:Z
```

Postcondición Sprint 0:

```text
temperatura = -12
```

---

## Clase EmisoraBLE

Estado privado:

```text
nombre:Texto
fabricanteID:N
potenciaRadio:Z
```

Diseño:

```text
------------------------- EmisoraBLE -------------------------
|
| nombre:Texto
| fabricanteID:N
| potenciaRadio:Z
|
|
nombreEmisora:Texto,
fabricante:N,
txPower:Z ---------> EmisoraBLE() -->
|
|
|                    encenderEmisora() -->
|
|
uuid:[N]_16,
major:N,
minor:N,
rssi1m:Z ----------> emitirAnuncioIBeacon() -->
|
|
|                    detenerAnuncio() -->
|
---------------------------------------------------------------
```

---

## Clase Publicador

Estado:

```text
beaconUUID:[N]_16
laEmisora:EmisoraBLE
RSSI:Z
```

Enumerado lógico:

```text
MedicionesID = {
    CO2=11,
    TEMPERATURA=12,
    RUIDO=13,
    O3=14
}
```

Diseño:

```text
-------------------------- Publicador --------------------------
|
| beaconUUID:[N]_16
| laEmisora:EmisoraBLE
| RSSI:Z
|
|
|                    Publicador() -->
|
|
|                    encenderEmisora() -->
|
|
valorPPB:Z,
contador:N,
tiempoEmisionMs:N --> publicarO3() -->
|
|
temperaturaC:Z,
contador:N,
tiempoEmisionMs:N --> publicarTemperatura() -->
|
|  privado:
|  idMedida:N, valor:Z, contador:N, tiempoEmisionMs:N
|       --> publicar() -->
|
----------------------------------------------------------------
```

### Algoritmo `publicar()`

Entrada:

```text
idMedida:N, valor:Z, contador:N, tiempoEmisionMs:N
```

Precondición:

```text
0 <= contador <= 255
```

Algoritmo:

```text
major <- (idMedida << 8) OR contador
minor <- representación de 16 bits de valor

emitirAnuncioIBeacon(beaconUUID, major, minor, RSSI)
esperar tiempoEmisionMs
detenerAnuncio()
```

---

## Programa principal

### `setup()`

```text
setup() --> sistemaInicializado:VoF
```

Algoritmo lógico:

```text
inicializar puerto serie
iniciarMedidor()
encenderEmisora()
sistemaInicializado <- VERDADERO
```

### `loop()`

```text
loop() -->
```

Algoritmo:

```text
contador <- contador + 1
valorO3 <- medirO3()
publicarO3(valorO3, contador, 1200)
esperar 300

temperatura <- medirTemperatura()
publicarTemperatura(temperatura, contador, 1200)
esperar 1500
```

## Protocolo producido

```text
Major = (idMedida << 8) OR contador
Minor = valor
```

Sprint 0:

```text
ID 14 --> O3 = 123
ID 12 --> Temperatura = -12
```
