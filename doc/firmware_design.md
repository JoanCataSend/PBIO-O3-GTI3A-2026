# firmware_design.md

## Component Design (Diseño del Componente)

**Componente:** `firmware`
**Implementación canónica para revisión:** `src/firmware/`
**Código operativo equivalente:** `firmware/NodoO3/`

El componente genera medidas ficticias reproducibles para Sprint 0 y las publica como iBeacon BLE. El programa principal coordina tres clases: `Medidor`, `Publicador` y `EmisoraBLE`.


### Tipos lógicos comunes

```text
N      número natural
Z      número entero
R      número real
VoF    booleano
Texto  cadena de caracteres
[T]    colección de T
[T]_n  array de T de tamaño fijo n
JSON   Texto con estructura JSON
```

Tipos del dominio:

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
```


### Arquitectura

```text
NodoO3
  |
  +--> Medidor
  |
  +--> Publicador
          |
          +--> EmisoraBLE
```

### Contratos públicos y privados

```text
Medidor() -->
iniciarMedidor() -->
medirO3() --> valorO3:Z
medirTemperatura() --> temperatura:Z

EmisoraBLE(nombre:Texto, fabricanteID:N, potenciaRadio:Z) -->
encenderEmisora() -->
emitirAnuncioIBeacon(uuid:[N]_16, major:N, minor:N, rssi1m:Z) -->
detenerAnuncio() -->

Publicador() -->
encenderEmisora() -->
publicarO3(valorPPB:Z, contador:N, tiempoEmisionMs:N) -->
publicarTemperatura(temperaturaC:Z, contador:N, tiempoEmisionMs:N) -->
publicar(idMedida:N, valor:Z, contador:N, tiempoEmisionMs:N) -->   [privada]
```

### Algoritmo de publicación

```text
Precondición: 0 <= contador <= 255
major <- (idMedida << 8) OR contador
minor <- representación de 16 bits de valor
emitirAnuncioIBeacon(UUID_PROYECTO, major, minor, RSSI)
esperar tiempoEmisionMs
detenerAnuncio()
```

Constantes de protocolo:

```text
NOMBRE_NODO = "GTI Joan"
UUID_PROYECTO = ASCII("EPSG-GTI-PROY-3A")
ID_CO2 = 11
ID_TEMPERATURA = 12
ID_RUIDO = 13
ID_O3 = 14
O3_FAKE = 123 ppb
TEMPERATURA_FAKE = -12 °C
```

`setup()` inicializa puerto serie, medidor y BLE. `loop()` incrementa el contador, publica O3 durante 1200 ms, espera 300 ms, publica temperatura durante 1200 ms y espera 1500 ms.

## Design Clarifications (Aclaraciones del Diseño)

- Sprint 0 usa valores estáticos (`123` y `-12`) de forma deliberada para demostrar el flujo completo sin depender de la calibración del sensor real.
- `Major` empaqueta el identificador de medida en los 8 bits altos y el contador en los 8 bits bajos.
- `Minor` conserva el patrón de 16 bits del valor; Android interpreta O3 sin signo y temperatura como entero con signo de 16 bits.
- El advertising es no conectable y escaneable; el fabricante es `0x004C` y el intervalo se configura en 100 ms.
- El componente no conoce HTTP, MariaDB ni la interfaz gráfica.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** C++ para Arduino, usando el core Adafruit nRF52 y Bluefruit.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** cada función o método incluye su diseño lógico en un bloque de comentario delimitado por `--------------------` inmediatamente antes de la implementación.
- **Code Readability / Legibilidad del Código:** clases con responsabilidad única, constantes de protocolo explícitas, nombres descriptivos y lógica de codificación separada del hardware BLE.
- **Automated Testing / Pruebas Automatizadas:** `src/firmware/tests/firmware_contract_test.py` comprueba automáticamente los valores fake, los IDs y la fórmula de codificación `Major`; la decodificación complementaria también se prueba en los tests Android.
- **Source correspondence / Correspondencia:** `src/firmware/` contiene una copia exacta y normalizada del firmware operativo de `firmware/NodoO3/` para que el revisor pueda asociar directamente este diseño con su implementación.
