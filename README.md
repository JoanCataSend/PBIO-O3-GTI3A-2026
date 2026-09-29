# PBIO – Nodo O₃ “GTI Joan”

Prototipo de adquisición y transmisión BLE para la asignatura **Proyecto de Aplicaciones de Biometría y Medio Ambiente (PBIO)**.

El sistema usa una **SparkFun Pro nRF52840 Mini** y un módulo **SPEC Sensors ULPSM-O3**. El firmware adquiere `Vgas`, `Vref` y `Vtemp`, calcula O₃ y temperatura, filtra la medida de O₃ y publica ambas magnitudes mediante **iBeacon**. La aplicación Android busca únicamente el nodo `GTI Joan`, decodifica la trama y muestra los valores recibidos.

## Estructura

```text
.
├── README.md
├── .gitignore
├── docs/
│   ├── architecture.md
│   ├── hardware.md
│   ├── sensor-calibration.md
│   └── validation.md
├── firmware/
│   └── NodoO3/
│       ├── NodoO3.ino
│       ├── Medidor.h
│       ├── Publicador.h
│       └── EmisoraBLE.h
└── android/
    └── proyecto Android Studio completo
```

La estructura es deliberadamente simple:

- `firmware/`: código que corre en la nRF52840.
- `android/`: aplicación receptora.
- `docs/`: documentación técnica y pruebas.
- raíz: solo archivos de entrada al repositorio.

## Identificación BLE

Nombre anunciado:

```text
GTI Joan
```

UUID del proyecto:

```text
EPSG-GTI-PROY-3A
```

Formato:

```text
Major = [ID medida: 8 bits][contador: 8 bits]
Minor = valor de la medida
```

IDs usados:

| Medida | ID |
|---|---:|
| CO₂ | 11 |
| Temperatura | 12 |
| Ruido | 13 |
| O₃ | 14 |

## Firmware

Abrir en Arduino IDE:

```text
firmware/NodoO3/NodoO3.ino
```

Configuración utilizada durante el desarrollo:

- Adafruit nRF52 Boards `1.7.0`
- variante SparkFun nRF52840 Mini
- Bluefruit52Lib
- Adafruit TinyUSB `3.6.0`

## Android

Abrir en Android Studio la carpeta:

```text
android/
```

Para la configuración Gradle incluida se recomienda **JDK 17**.

Las pruebas BLE deben hacerse con un **teléfono Android físico**.

## Documentación

- `docs/architecture.md`: flujo del sistema y protocolo.
- `docs/hardware.md`: conexiones y adquisición analógica.
- `docs/sensor-calibration.md`: parámetros específicos del sensor.
- `docs/validation.md`: plan de comprobación y pruebas.

## Estado

Comprobado:

- adquisición analógica;
- cálculo de temperatura;
- cálculo y filtrado de O₃;
- publicación iBeacon;
- recepción Android;
- decodificación de `Major`, `Minor`, ID y contador;
- visualización de O₃ y temperatura.

La concentración absoluta de O₃ no se considera calibrada metrológicamente: el prototipo usa la sensibilidad individual del sensor, pero el cero actual se aproxima mediante `Vgas0 = Vref`.
