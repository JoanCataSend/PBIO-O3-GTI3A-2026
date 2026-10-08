# firmware_design.md

## Diseño del Componente

**Componente:** `firmware`  
**Implementación:** `src/firmware/`  
**Lenguaje:** C++ para Arduino / Adafruit nRF52.

Este diseño recoge la arquitectura lógica del firmware del Sprint 0: adquisición ficticia, codificación del protocolo y publicación BLE se mantienen separadas.

### Tipos lógicos

```text
UUIDProyecto = [N]_16
MedicionId = { TEMPERATURA, O3 }
```

Constantes del Sprint 0:

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

### Clase `Medidor`

No mantiene estado de dominio.

```text
                 -------- Medidor --------
                 |
                 |
                 Medidor() -->
                 |
                 |
                 iniciarMedidor() -->
                 |
                 |
valor_o3: N   <-- medirO3() -->
                 |
                 |
temperatura: Z <-- medirTemperatura() -->
                 |
                 -------------------------
```

El lado derecho usa `-->` porque estas operaciones realizan efectos externos por puerto serie.

Contratos:

```text
medirO3() --> valor_o3: N
medirTemperatura() --> temperatura: Z
```

Postcondiciones del Sprint 0:

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
                      -------- EmisoraBLE --------
                      | nombre: Text
                      | fabricante_id: N
                      | potencia_radio: Z
                      |
nombre_emisora: Text, |
fabricante: N,        |
tx_power: Z       --> EmisoraBLE() -->
                      |
                      |
                      encenderEmisora() -->
                      |
                      |
uuid: [N]_16,         |
major: N,             |
minor: N,             |
rssi_1m: Z        --> emitirAnuncioIBeacon() -->
                      |
                      |
                      detenerAnuncio() -->
                      |
                      ----------------------------
```

### Clase `Publicador`

Estado privado:

```text
beacon_uuid: [N]_16
la_emisora: EmisoraBLE
rssi_1m: Z
```

```text
                         -------- Publicador --------
                         | beacon_uuid: [N]_16
                         | la_emisora: EmisoraBLE
                         | rssi_1m: Z
                         |
id_medida: N,           |
valor: Z,               |
contador: N,            |
tiempo_emision_ms: N --> publicar() -->
                         |
                         |
                         Publicador() -->
                         |
                         |
                         encenderEmisora() -->
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
                         -----------------------------
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
setup()
loop()
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

- El diseño lógico usa variables en minúsculas con guion bajo; los identificadores C++ pueden conservar `camelCase`.
- El Sprint 0 usa medidas ficticias editables en `Medidor.h` para la prueba extremo a extremo.
- `Major` reserva 8 bits para el tipo y 8 para el contador; `Minor` transporta los 16 bits del valor.
- Solo se conservan los IDs realmente emitidos: 12 y 14.
- `EmisoraBLE` encapsula Bluefruit; `Medidor` no conoce BLE y `NodoO3.ino` no construye tramas.

## Reglas Generales

- **Lenguaje de Programación:** C++ para Arduino con Bluefruit52Lib.
- **Encabezados de Funciones/Métodos:** cada función o método propio debe incluir su diseño lógico dentro de `--------------------` y una breve descripción.
- **Legibilidad del Código:** mantener responsabilidades segregadas y nombres semánticos.
- **Pruebas Automatizadas:** `src/firmware/tests/firmware_contract_test.py` verifica IDs, valores ficticios, UUID y empaquetado de Major; el flujo BLE se valida además en la prueba presencial.
