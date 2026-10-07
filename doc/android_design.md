# android_design.md

## Diseño del Componente

**Componente:** `android`  
**Implementación:** `src/android/`  
**Lenguaje:** Java.

El componente Android recibe anuncios iBeacon, extrae la medida, actualiza la interfaz y llama a una lógica fake que representa la interfaz remota de la lógica de negocio. La actividad no construye HTTP directamente.

### Tipos lógicos

```text
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

Constantes de dominio:

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

Los atributos son privados. El constructor modifica el estado de la instancia y `toJson()` solo lo consulta.

```text
                 -------- MedidaEntrada --------
                 | uuid: Text
                 | tipo_medida_id: N
                 | valor: Z
                 | contador: N
                 | rssi: Z
                 |
uuid: Text,      |
tipo_medida_id: N,
valor: Z,
contador: N,
rssi: Z      --> MedidaEntrada() -->
                 |
                 |
      texto: Text <-- toJson() <--
                 |
                 -------------------------------
```

Firma matemática equivalente del serializador:

```text
toJson() --> texto: Text
```

### Clase `TramaIBeacon`

El constructor copia los bytes recibidos antes de analizarlos. Los getters de arrays devuelven copias para que el estado privado no pueda modificarse desde fuera.

```text
                    -------- TramaIBeacon --------
                    | los_bytes: [N]
                    | uuid: [N]_16
                    | major: [N]_2
                    | minor: [N]_2
                    | tx_power: Z
                    | valida: B
                    | analizar() -->
                    |
bytes: [N]       --> TramaIBeacon() -->
                    |
                    |
         valida: B <-- esValida() <--
                    |
                    |
      uuid: [N]_16 <-- getUUID() <--
                    |
                    |
       major: [N]_2 <-- getMajor() <--
                    |
                    |
       minor: [N]_2 <-- getMinor() <--
                    |
                    |
         tx_power: Z <-- getTxPower() <--
                    |
                    |
         bytes: [N] <-- getLosBytes() <--
                    |
                    --------------------------------
```

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

No mantiene estado de instancia. Todas las operaciones son independientes de estado (`--x`).

```text
                 ---------- Utilidades ----------
                 |
bytes: [N]    --> bytesToString() --x
texto: Text   <--
                 |
                 |
bytes: [N]    --> bytesToHexString() --x
hexadecimal: Text <--
                 |
                 |
bytes: [N]    --> bytesToUnsignedInt() --x
valor: N      <--
                 |
                 |
bytes: [N]_2  --> bytesToSignedInt16() --x
valor: Z      <--
                 |
                 --------------------------------
```

### Clase estática `LogicaFake`

El callback Java es un mecanismo de implementación y se elimina del diseño lógico, tal como exige la notación oficial.

```text
                 ---------- LogicaFake ----------
                 |
                 | comprobarServidor() --x
estado: Text     <--
                 |
                 |
datos: MedidaEntrada --> insertarMedida() --x
medida: MedidaVista  <--
                 |
                 -------------------------------
```

Firma matemática equivalente:

```text
comprobarServidor() --> estado: Text
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
```

### Clase estática `PeticionarioREST`

La asincronía, el `ExecutorService` y los callbacks son detalles de implementación. El contrato lógico conserva solo entradas y salidas.

```text
                 -------- PeticionarioREST --------
                 |
url: Text      --> getJson() --x
respuesta: Text <--
                 |
                 |
url: Text,       |
datos: Text   --> postJson() --x
respuesta: Text <--
                 |
                 |
                 | entrada: Text --> leerTexto() --x
                 | texto: Text <--
                 |
                 ----------------------------------
```

Firmas matemáticas equivalentes:

```text
url: Text --> getJson() --> respuesta: Text
url: Text, datos: Text --> postJson() --> respuesta: Text
entrada: Text --> leerTexto() --> texto: Text
```

### Clase `MainActivity`

El diseño lógico omite referencias `View`, `Bundle`, callbacks, hilos y objetos del framework. Solo se representan datos de dominio y estado propio relevante.
La actividad realiza además una comprobación de conectividad REST al iniciar y muestra el motivo concreto de cualquier fallo de red/HTTP/TLS.

Estado privado relevante:

```text
ultimo_contador_o3_enviado: Z
ultimo_contador_temperatura_enviado: Z
```

Interfaz/callbacks de ciclo de vida con efectos laterales:

```text
                     -------- MainActivity --------
                     | ultimo_contador_o3_enviado: Z
                     | ultimo_contador_temperatura_enviado: Z
                     |
                     | onCreate() -->
                     |
                     |
                     | botonBuscarNuestroDispositivoBTLEPulsado() -->
                     |
                     |
                     | botonDetenerBusquedaDispositivosBTLEPulsado() -->
                     |
                     |
resultado: ResultadoPermisos --> onRequestPermissionsResult() -->
                     |
                     |
                     | onResume() -->
                     |
                     |
                     | onDestroy() -->
                     |
                     --------------------------------
```

Operaciones privadas y callbacks internos, expresados como firmas matemáticas:

```text
tengoPermisosBLE() --> concedidos: B
pedirPermisosSiHacenFalta()
comprobarBluetooth()
iniciarBusqueda()
detenerBusqueda()
resultado: ResultadoBLE --> onScanResult()
error_code: N --> onScanFailed()
resultado: ResultadoBLE --> procesarResultado()
uuid: Text, id_medida: N, valor: Z, contador: N, rssi: Z --> enviarMedidaSiEsNueva()
respuesta: MedidaVista --> correcto()
mensaje: Text --> error()
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

- La notación oficial representa variables en minúsculas (`tipo_medida_id`, `fecha_hora`); Java/JSON conserva los identificadores de implementación `tipoMedidaId`, `fechaHora`, etc. Es una correspondencia uno-a-uno, no un cambio de tipo ni responsabilidad.
- El package final es `es.upv.jcatsen.pbio`; se eliminó el identificador heredado del proyecto de ejemplo.
- Se conserva Java y la pila Android existente para no introducir una migración de framework innecesaria durante el Sprint 0.
- El escaneo BLE debe probarse en un teléfono con radio BLE real; un AVD puede no entregar anuncios físicos.
- O3 se interpreta como `N` de 16 bits; temperatura como `Z` de 16 bits.
- Los parámetros `View`/`Bundle`, callbacks e hilos se omiten porque son mecanismos de implementación y no datos lógicos.
- Los arrays devueltos por `TramaIBeacon` son copias defensivas, manteniendo la encapsulación indicada por el diagrama de clase.


- **Trazabilidad de ingeniería inversa:** `MainActivity`, `TramaIBeacon` y `Utilidades` conservan las responsabilidades del código Android proporcionado como base y están representadas explícitamente en este diseño. `MedidaEntrada`, `LogicaFake` y `PeticionarioREST` corresponden a la ampliación del Sprint 0 para conectar el cliente con el backend.

## Reglas Generales

- **Lenguaje de Programación:** Java para Android.
- **Encabezados de Funciones/Métodos:** cada función o método de autoría propia debe incluir su diseño lógico entre dos líneas `--------------------` y una breve descripción; cuando no exista entrada o salida lógica, se omite la flecha correspondiente.
- **Legibilidad del Código:** mantener nombres semánticos, responsabilidades separadas y evitar comentarios que solo repitan el código.
- **Pruebas Automatizadas:** generar pruebas unitarias o de integración para operaciones clave; la recepción por radio, que depende de hardware externo, se completa mediante el test de aceptación presencial.
