# android_design.md

## Diseño del Componente

**Componente:** `android`  
**Implementación:** `src/android/`  
**Lenguaje:** Java.

El componente Android implementa la GUI móvil y la recepción BLE. Decodifica la trama iBeacon y entrega las operaciones remotas exclusivamente a `frontend_business_logic`; no contiene código de transporte remoto.

### Tipos lógicos propios del componente

```text
ResultadoBLE = (
    bytes: [N],
    rssi: Z
)

ResultadoPermisos = (
    request_code: N,
    permisos: [Text],
    resultados: [Z]
)
```

Tipos consumidos desde `frontend_business_logic_design.md`:

```text
MedidaEntrada
MedidaVista
EstadoBD
```

Constantes de protocolo:

```text
NOMBRE_NODO = "GTI Joan"
UUID_PROYECTO = "EPSG-GTI-PROY-3A"
ID_TEMPERATURA = 12
ID_O3 = 14
```

### Arquitectura

```text
MainActivity
   |
   +--> TramaIBeacon
   +--> Utilidades
   |
   +--> frontend_business_logic.LogicaFake
```

`MainActivity` no utiliza directamente URLs, `fetch`, conexiones ni clases de transporte.

### Clase `TramaIBeacon`

Estado privado:

```text
los_bytes: [N]
uuid: [N]_16
major: [N]_2
minor: [N]_2
tx_power: Z
valida: B
```

```text
                    -------- TramaIBeacon --------
                    | los_bytes: [N]
                    | uuid: [N]_16
                    | major: [N]_2
                    | minor: [N]_2
                    | tx_power: Z
                    | valida: B
                    |
                    | analizar() -->
                    |
bytes: [N]      --> TramaIBeacon() -->
                    |
        valida: B <-- esValida() <--
                    |
   uuid: [N]_16 <-- getUUID() <--
                    |
    major: [N]_2 <-- getMajor() <--
                    |
    minor: [N]_2 <-- getMinor() <--
                    |
      tx_power: Z <-- getTxPower() <--
                    |
      bytes: [N] <-- getLosBytes() <--
                    |
                    ------------------------------
```

`analizar()` es privado y modifica el estado de la instancia; los getters solo lo inspeccionan.

Algoritmo lógico de `analizar()`:

```text
buscar la secuencia 4C 00 02 15
si no existe -> valida <- false
si existe y hay 25 bytes desde el prefijo:
    uuid <- 16 bytes siguientes
    major <- 2 bytes siguientes
    minor <- 2 bytes siguientes
    tx_power <- byte siguiente
    valida <- true
```

### Clase estática `Utilidades`

```text
bytes: [N]   --> bytesToString() --> texto: Text      --x
bytes: [N]   --> bytesToHexString() --> hexadecimal: Text --x
bytes: [N]   --> bytesToUnsignedInt() --> valor: N    --x
bytes: [N]_2 --> bytesToSignedInt16() --> valor: Z    --x
```

### `MainActivity`

Estado relevante:

```text
ultimo_contador_o3_enviado: Z
ultimo_contador_temperatura_enviado: Z
```

Operaciones principales:

```text
onCreate()
comprobarServidor()
tengoPermisosBLE() --> concedidos: B
pedirPermisosSiHacenFalta()
comprobarBluetooth()
botonBuscarNuestroDispositivoBTLEPulsado()
botonDetenerBusquedaDispositivosBTLEPulsado()
iniciarBusqueda()
detenerBusqueda()
resultado: ResultadoBLE --> procesarResultado()
uuid: Text, id_medida: N, valor: Z, contador: N, rssi: Z --> enviarMedidaSiEsNueva()
resultado: ResultadoPermisos --> onRequestPermissionsResult()
onResume()
onDestroy()
```

Algoritmo lógico de `procesarResultado()`:

```text
si no hay scan record -> terminar
trama <- TramaIBeacon(bytes)
si trama no válida -> terminar
uuid <- bytesToString(trama.uuid)
si uuid != UUID_PROYECTO -> terminar
major <- bytesToUnsignedInt(trama.major)
id_medida <- 8 bits altos de major
contador <- 8 bits bajos de major
si id_medida = ID_O3:
    valor <- bytesToUnsignedInt(trama.minor)
si id_medida = ID_TEMPERATURA:
    valor <- bytesToSignedInt16(trama.minor)
en otro caso -> no enviar
crear MedidaEntrada
enviarMedidaSiEsNueva(...)
actualizar UI
```

Algoritmo lógico de `enviarMedidaSiEsNueva()`:

```text
si id_medida no es O3 ni Temperatura -> terminar
si contador ya enviado para ese tipo -> terminar
recordar contador
insertarMedida(datos) mediante frontend_business_logic
si la operación falla -> liberar ese contador para permitir reintento
```

### Descripción textual de la GUI Android

Una única pantalla muestra estado BLE, botones **Buscar GTI Joan** y **Detener búsqueda**, último O3, temperatura, contador, RSSI, trama decodificada y estado de la operación remota.

## Aclaraciones del Diseño

- Los parámetros Android propios del framework (`View`, callbacks del sistema, hilos y objetos de escaneo) se omiten en el diseño lógico cuando no pertenecen al contrato de dominio.
- La comunicación remota está implementada en `src/frontend_business_logic/android/`; `src/android/` depende de esa interfaz pero no de `PeticionarioREST`.
- El package de la aplicación es `es.upv.jcatsen.pbio`.
- La recepción BLE debe probarse en un teléfono físico; un emulador puede no entregar anuncios reales.
- O3 se interpreta como `N` de 16 bits y temperatura como `Z` de 16 bits.

## Reglas Generales

- **Lenguaje de Programación:** Java para Android.
- **Encabezados de Funciones/Métodos:** cada método propio debe incluir diseño lógico dentro de `--------------------` y una breve descripción.
- **Legibilidad del Código:** `MainActivity` coordina GUI/BLE, `TramaIBeacon` analiza la trama y `Utilidades` realiza conversiones; la lógica de negocio del cliente reside fuera del componente.
- **Pruebas Automatizadas:** JUnit cubre protocolo BLE y la integración estática con `frontend_business_logic`; el test instrumentado valida el package instalado.
