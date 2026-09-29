# PBIO – Nodo O₃ con iBeacon y aplicación Android

Proyecto de **PBIO / Sprint 0** basado en la arquitectura entregada por el profesorado.  
El sistema adquiere un sensor analógico de ozono mediante una **SparkFun Pro nRF52840 Mini**, calcula la medida y la publica mediante **iBeacon**. Una aplicación Android recibe la trama, decodifica `Major` / `Minor` y muestra **O₃** y **temperatura**.

## Estructura del repositorio

```text
Arduino-O3/
└── NodoO3/
    ├── NodoO3.ino
    ├── Medidor.h
    ├── Publicador.h
    └── EmisoraBLE.h

Android/
└── Prueba2025/
    └── proyecto Android Studio completo
```

## Hardware

- SparkFun Pro nRF52840 Mini
- Sensor SPEC Sensors ULPSM-O3
- Alimentación del sensor a 3.3 V

### Conexiones

| ULPSM-O3 | SparkFun nRF52840 |
|---|---|
| Vgas | pin 5 / P0.05 / AIN3 |
| Vref | pin 28 / P0.28 / AIN4 |
| Vtemp | pin 29 / P0.29 / AIN5 |
| GND | GND |
| V+ | 3V3 |

## Sensor concreto utilizado

Data Matrix leído en la unidad empleada:

```text
032824010609 110406 O3 2404 -74.04
```

Datos empleados en el cálculo:

- Gas: O₃
- Sensibilidad individual: **-74.04 nA/ppm**
- TIA Gain del módulo: **499 kV/A**
- Factor de conversión:

```text
M = -74.04 × 499 × 10^-6
M = -0.03694596 V/ppm
```

> El valor `BIAS = -25 mV` indicado en la etiqueta corresponde a la polarización interna del sensor y **no** se usa como `Voffset`.

### Referencia de cero

La versión actual usa:

```text
Voffset = 0
Vgas0 = Vref
```

Esto permite trabajar con una referencia inicial coherente. Para una calibración absoluta de concentración sería necesario determinar `Vgas0` en una atmósfera de referencia libre de O₃.

## Procesamiento de la medida

1. Lectura de `Vgas`, `Vref` y `Vtemp`.
2. Promedio de 50 lecturas ADC.
3. Estimación de temperatura.
4. Conversión de `Vgas - Vgas0` a concentración usando la sensibilidad individual.
5. Compensación típica por temperatura.
6. Estabilización de O₃:
   - mediana de las últimas 5 medidas;
   - filtro exponencial con `alpha = 0.25`.
7. Publicación mediante iBeacon.

## Formato iBeacon

UUID:

```text
EPSG-GTI-PROY-3A
```

Nombre BLE:

```text
GTI3A-2025
```

El formato mantiene la arquitectura del código entregado por el profesorado:

```text
Major = [ID medida: 8 bits][contador: 8 bits]
Minor = valor de la medida
```

IDs utilizados:

| Medida | ID |
|---|---:|
| CO2 | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O₃ | 14 |

Ejemplo:

```text
ID O3 = 14
contador = 16

Major = (14 << 8) | 16 = 3600
Minor = 291
```

La aplicación Android interpreta entonces:

```text
ID = 14 -> O3 = 291 ppb
```

## Arduino

### Requisitos

- Arduino IDE
- Adafruit nRF52 Boards `1.7.0`
- Adafruit TinyUSB `3.6.0`
- Bluefruit52Lib incluida con el core nRF52

Abrir:

```text
Arduino-O3/NodoO3/NodoO3.ino
```

Los cuatro archivos del sketch deben permanecer en la misma carpeta.

## Android

Abrir en Android Studio:

```text
Android/Prueba2025
```

El proyecto usa el paquete:

```text
org.jordi.prueba2025
```

Para este proyecto antiguo, usar **JDK 17** con su versión de Gradle.

La prueba BLE debe hacerse con un **teléfono Android físico**. El emulador no es adecuado para recibir el beacon físico.

La app:

- busca `GTI3A-2025`;
- localiza la trama iBeacon;
- valida el UUID `EPSG-GTI-PROY-3A`;
- separa `Major` en `ID` y `contador`;
- interpreta `Minor` como valor de medida;
- conserva en pantalla la última medida recibida de O₃ y temperatura.

## Estado comprobado

Se ha verificado experimentalmente:

- lectura ADC de `Vgas`, `Vref` y `Vtemp`;
- cálculo de temperatura;
- cálculo y filtrado de O₃;
- advertising iBeacon;
- recepción en Android;
- decodificación correcta de `Major`, `Minor`, ID y contador;
- visualización de O₃ y temperatura.

## Observación sobre exactitud

El filtrado aplicado reduce picos y estabiliza la lectura mostrada, pero **no sustituye una calibración metrológica**. La sensibilidad individual de la unidad sí se utiliza en el cálculo, mientras que el cero actual se aproxima con `Vgas0 = Vref`.
