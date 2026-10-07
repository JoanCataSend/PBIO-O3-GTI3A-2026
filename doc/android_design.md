# android_design.md

## Diseño del Componente

**Componente:** `android`  
**Implementación:** `src/android/`  
**Lenguaje:** Java.

El componente Android recibe iBeacon, extrae la medida, actualiza la interfaz y llama a una lógica fake que representa la interfaz remota de la lógica de negocio. La actividad no construye HTTP directamente.

### Tipos lógicos

```text
Json = Text

MedidaEntrada = (
    uuid: Text,
    tipo_medida_id: N,
    valor: Z,
    contador: N,
    rssi: Z
)

MedidaVista = (
    medida_id: N,
    dispositivo_id: N,
    uuid: Text,
    dispositivo: Text,
    tipo_medida_id: N,
    tipo_medida: Text,
    unidad: Text,
    valor: Z,
    contador: N,
    rssi: Z,
    fecha_hora: Text
)

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

Constantes:

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
   +--> MedidaEntrada
   +--> LogicaFake
             |
             +--> PeticionarioREST
```

### Clase `MedidaEntrada`

```text
                    -------- MedidaEntrada --------
                    | uuid: Text
                    | tipo_medida_id: N
                    | valor: Z
                    | contador: N
                    | rssi: Z
                    |
uuid: Text,          |
tipo_medida_id: N, |
valor: Z,           |
contador: N,        |
rssi: Z         --> MedidaEntrada() -->
                    |
                    |
         json: Json <-- toJson() <--
                    |
                    -------------------------------
```

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

`analizar()` es privado y muta ese estado.

```text
                     ---------- TramaIBeacon ----------
                     | los_bytes: [N]
                     | uuid: [N]_16
                     | major: [N]_2
                     | minor: [N]_2
                     | tx_power: Z
                     | valida: B
                     |
                     | analizar() -->
                     |
bytes: [N]       --> TramaIBeacon() -->
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
                     ---------------------------------
```

Algoritmo de `analizar()`:

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

### `LogicaFake`

```text
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista --x
```

Responsabilidad:

```text
MedidaEntrada -> serializar -> PeticionarioREST -> respuesta de dominio
```

### `PeticionarioREST`

La asincronía y el callback son mecanismos Java; el contrato lógico elimina ese detalle:

```text
url: Text, datos: Json --> postJson() --> respuesta: Json --x
entrada: Text --> leerTexto() --> texto: Text
```

### `MainActivity`

Estado relevante:

```text
ultimo_contador_o3_enviado: Z
ultimo_contador_temperatura_enviado: Z
```

Operaciones principales:

```text
onCreate() -->
tengoPermisosBLE() --> concedidos: B
pedirPermisosSiHacenFalta() -->
comprobarBluetooth() -->
botonBuscarNuestroDispositivoBTLEPulsado() -->
botonDetenerBusquedaDispositivosBTLEPulsado() -->
iniciarBusqueda() -->
detenerBusqueda() -->
resultado: ResultadoBLE --> procesarResultado() -->
uuid: Text, id_medida: N, valor: Z, contador: N, rssi: Z --> enviarMedidaSiEsNueva() -->
resultado: ResultadoPermisos --> onRequestPermissionsResult() -->
onResume() -->
onDestroy() -->
```

Algoritmo de `procesarResultado()`:

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
enviarMedidaSiEsNueva(uuid, id_medida, valor, contador, rssi)
actualizar UI
```

Algoritmo de `enviarMedidaSiEsNueva()`:

```text
si id_medida no es O3 ni Temperatura -> terminar
si contador ya enviado para ese tipo -> terminar
recordar contador
datos <- MedidaEntrada(...)
insertarMedida(datos)
si la petición falla -> liberar ese contador para permitir reintento
```

### Descripción textual de la GUI

Una única pantalla muestra estado BLE, botones **Buscar GTI Joan** y **Detener búsqueda**, último O3, temperatura, contador, RSSI, trama decodificada y estado del servidor.

## Aclaraciones del Diseño

- La notación oficial expresa variables en minúsculas con guion bajo (`tipo_medida_id`, `fecha_hora`); Java/JSON conserva los identificadores de implementación `tipoMedidaId`, `fechaHora`, etc. Es una correspondencia de nombres, no un cambio de tipo ni responsabilidad.
- El package del proyecto final es `es.upv.jcatsen.pbio`; se eliminó el identificador heredado `org.jordi.prueba2025`.
- Se conserva Java y la pila Android original para no introducir riesgo de migración durante el Sprint 0.
- El escaneo debe probarse en un teléfono con BLE real; un AVD puede no entregar anuncios BLE.
- O3 se interpreta como `N` de 16 bits; temperatura como `Z` de 16 bits.
- Los parámetros `View`, callbacks e hilos no aparecen en el diseño lógico porque son detalles de implementación.

## Reglas Generales

- **Lenguaje de Programación:** Java para Android.
- **Encabezados de Funciones/Métodos:** cada método debe incluir diseño lógico entre `--------------------` y breve descripción; se omiten detalles propios del framework que no forman parte del contrato lógico.
- **Legibilidad del Código:** `MainActivity` coordina UI/BLE; `TramaIBeacon` analiza; `Utilidades` convierte; `LogicaFake` expone el contrato remoto; `PeticionarioREST` hace HTTP.
- **Pruebas Automatizadas:** JUnit cubre Major/Minor y URL; el test instrumentado valida el package instalado. La recepción BLE se valida de forma presencial con hardware real.
