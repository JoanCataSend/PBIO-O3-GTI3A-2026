# firmware_design.md

## Diseño del Componente

**Componente:** `firmware`  
**Implementación:** `src/firmware/`  
**Lenguaje:** C++ para Arduino / Adafruit nRF52.

Este diseño recoge la arquitectura obtenida por ingeniería inversa del firmware proporcionado y su adaptación al Sprint 0. Se mantiene la separación entre adquisición, traducción al protocolo y radio BLE.

### Tipos lógicos

```text
UUIDProyecto = [N]_16

MedicionId = { TEMPERATURA, O3 }
```

Constantes de protocolo:

```text
UUID_PROYECTO = "EPSG-GTI-PROY-3A"
ID_TEMPERATURA = 12
ID_O3 = 14
O3_FAKE = 123
TEMPERATURA_FAKE = -12
```

### Arquitectura

```text
NodoO3.ino
   |
   +--> Medidor
   |
   +--> Publicador
           |
           +--> EmisoraBLE
```

`NodoO3.ino` coordina el ciclo. `Medidor` produce las medidas ficticias. `Publicador` codifica Major/Minor. `EmisoraBLE` es la única clase que conoce Bluefruit y el advertising.

### Clase `Medidor`

No mantiene estado de dominio.

```text
                 -------- Medidor --------
                 |
                 |
              --> Medidor() -->
                 |
                 |
              --> iniciarMedidor() -->
                 |
                 |
valor_o3: N  <-- medirO3() -->
                 |
                 |
temperatura: Z <-- medirTemperatura() -->
                 |
                 -------------------------
```

Contratos:

```text
medirO3() --> valor_o3: N
medirTemperatura() --> temperatura: Z
```

Postcondiciones Sprint 0:

```text
valor_o3 = 123
temperatura = -12
```

### Clase `EmisoraBLE`

Estado privado:

```text
nombre: Text
fabricante_id: N
potencia_radio: Z
```

```text
                      ----------- EmisoraBLE -----------
                      |
                      | nombre: Text
                      | fabricante_id: N
                      | potencia_radio: Z
                      |
nombre_emisora: Text, |
fabricante: N,        |
tx_power: Z       --> EmisoraBLE() -->
                      |
                      |
                   --> encenderEmisora() -->
                      |
                      |
uuid: [N]_16,         |
major: N,             |
minor: N,             |
rssi_1m: Z        --> emitirAnuncioIBeacon() -->
                      |
                      |
                   --> detenerAnuncio() -->
                      |
                      ----------------------------------
```

### Clase `Publicador`

Estado privado:

```text
beacon_uuid: [N]_16
la_emisora: EmisoraBLE
rssi_1m: Z
```

El método privado `publicar()` queda encapsulado y es reutilizado por las dos operaciones públicas.

```text
                         ------------ Publicador ------------
                         |
                         | beacon_uuid: [N]_16
                         | la_emisora: EmisoraBLE
                         | rssi_1m: Z
                         |
                         | id_medida: N, valor: Z,
                         | contador: N, tiempo_emision_ms: N
                         | --> publicar() -->
                         |
                      --> Publicador() -->
                         |
                         |
                      --> encenderEmisora() -->
                         |
                         |
valor_ppb: N,           |
contador: N,            |
tiempo_emision_ms: N --> publicarO3() -->
                         |
                         |
temperatura_c: Z,       |
contador: N,            |
tiempo_emision_ms: N --> publicarTemperatura() -->
                         |
                         -------------------------------------
```

Algoritmo lógico de `publicar()`:

```text
major <- (id_medida << 8) OR contador
minor <- representación de 16 bits de valor
emitirAnuncioIBeacon(beacon_uuid, major, minor, rssi_1m)
esperar tiempo_emision_ms
detenerAnuncio()
```

### Programa principal

```text
setup() -->
loop() -->
```

`setup()`:

```text
inicializar puerto serie
iniciarMedidor()
encenderEmisora()
```

`loop()`:

```text
contador <- contador + 1
valor_o3 <- medirO3()
publicarO3(valor_o3, contador, 1200)
esperar 300
temperatura <- medirTemperatura()
publicarTemperatura(temperatura, contador, 1200)
esperar 1500
```

## Aclaraciones del Diseño

- Los identificadores del código C++ pueden usar `camelCase`; en el diseño lógico se expresan como variables minúsculas con guion bajo, tal como exige la notación oficial.
- El Sprint 0 **no lee ADC ni sensor físico**: las medidas son constantes y editables en `Medidor.h` para la demostración presencial.
- `Major` reserva 8 bits para el tipo y 8 para el contador: `major = (id_medida << 8) OR contador`.
- `Minor` transporta los 16 bits del valor. O3 se interpreta sin signo y temperatura en complemento a dos.
- Solo se conservan los IDs realmente publicados en este Sprint (`12` y `14`); no se mantienen tipos sin uso.
- La radio BLE está encapsulada en `EmisoraBLE`; `Medidor` no conoce BLE y `NodoO3.ino` no construye tramas.

## Reglas Generales

- **Lenguaje de Programación:** C++ para Arduino con Bluefruit52Lib.
- **Encabezados de Funciones/Métodos:** cada función o método debe incluir su diseño lógico dentro de un bloque delimitado por líneas `--------------------` y una breve descripción.
- **Legibilidad del Código:** nombres semánticos y responsabilidades segregadas; evitar comentarios que repitan literalmente el código.
- **Pruebas Automatizadas:** `src/firmware/tests/firmware_contract_test.py` comprueba IDs, valores ficticios, UUID y empaquetado Major; la recepción BLE se valida además en Android y en la prueba presencial.
