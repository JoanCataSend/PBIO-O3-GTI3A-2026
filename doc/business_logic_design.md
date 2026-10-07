# business_logic_design.md

## Diseño del Componente

**Componente:** `business_logic`  
**Implementación:** `src/business_logic/`  
**Lenguaje:** PHP 8.0 o superior.

La lógica de negocio es la única capa que valida datos de dominio y accede a MariaDB. No conoce códigos HTTP, HTML ni elementos de interfaz.

### Tipos lógicos

```text
MedidaEntrada = (
    uuid: Text,
    tipo_medida_id: N,
    valor: Z,
    contador: N,
    rssi: Z
)

Dispositivo = (
    dispositivo_id: N,
    uuid: Text,
    nombre: Text
)

TipoMedida = (
    tipo_medida_id: N,
    nombre: Text,
    unidad: Text
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

FiltrosMedida = (
    dispositivo_id: N,
    tipo_medida_id: N,
    desde: Text,
    hasta: Text
)

Los campos de `FiltrosMedida` son opcionales a nivel de llamada; si están presentes, deben respetar los tipos indicados. La ausencia de un campo significa que ese criterio no se aplica.

EstadoBD = (
    ok: B,
    database: Text
)

ConexionBD = (
    activa: B
)

MedidaVistaBD = (
    medida_id: Text,
    dispositivo_id: Text,
    uuid: Text,
    dispositivo: Text,
    tipo_medida_id: Text,
    tipo_medida: Text,
    unidad: Text,
    valor: Text,
    contador: Text,
    rssi: Text,
    fecha_hora: Text
)
```

`ConexionBD` representa de forma abstracta el recurso de persistencia; el diseño no expresa punteros, handles ni detalles de `mysqli`.

### Operaciones

```text
conexionBD() --> conexion: ConexionBD
probarConexion() --> estado: EstadoBD
datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
medida_id: N --> buscarMedidaConId() --> medida: MedidaVista
filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
listarDispositivos() --> dispositivos: [Dispositivo]
listarTiposMedida() --> tipos: [TipoMedida]
dispositivo_id: N, tipo_medida_id: N --> buscarUltimaMedida() --> medida: MedidaVista
datos: MedidaEntrada --> validarMedidaEntrada() -->
consultaMedidaVista() --> consulta: Text
fila: MedidaVistaBD --> normalizarMedidaVista() --> medida: MedidaVista
```

### Validación de entrada

```text
uuid existe y tiene 16 caracteres
tipo_medida_id es entero y cabe en 8 bits
valor es entero y cabe en los 16 bits transportados por Minor
si tipo_medida_id = 14 (O3), 0 <= valor <= 65535
si tipo_medida_id = 12 (Temperatura), -32768 <= valor <= 32767
contador es entero y 0 <= contador <= 255
rssi es entero
```

La existencia real del dispositivo y del tipo se comprueba contra los catálogos de la BBDD antes de insertar.

### Inserción

```text
validarMedidaEntrada(datos)
resolver Dispositivo por uuid
si no existe -> Error
resolver TipoMedida por tipo_medida_id
si no existe -> Error
insertar Medida
recuperar la fila creada con buscarMedidaConId()
devolver MedidaVista normalizada
```

### Consulta

`consultaMedidaVista()` centraliza el `JOIN` entre `Medida`, `Dispositivo` y `TipoMedida` para que todas las lecturas produzcan el mismo contrato.

## Aclaraciones del Diseño

- Los nombres lógicos siguen la convención oficial (`tipo_medida_id`, `fecha_hora`); el contrato JSON/SQL existente usa `tipoMedidaId`, `fechaHora`, etc. La implementación mantiene una correspondencia uno-a-uno.
- Las excepciones de validación y de dominio se producen aquí; es responsabilidad del adaptador REST traducirlas a HTTP.
- `SDBaseDatos.php` contiene secretos locales y **no se versiona**. Solo se entrega `SDBaseDatos.example.php`.
- Los identificadores devueltos por `mysqli` se normalizan explícitamente a enteros para respetar `N/Z` del diseño.
- Los filtros opcionales se validan antes de formar la consulta y los valores se enlazan mediante sentencias preparadas.

## Reglas Generales

- **Lenguaje de Programación:** PHP 8.0 o superior con `declare(strict_types=1)` y `mysqli`.
- **Encabezados de Funciones/Métodos:** cada función debe incluir su firma lógica entre `--------------------` y una breve descripción.
- **Legibilidad del Código:** validación, persistencia, consulta y normalización deben permanecer separadas; no introducir HTTP en esta capa.
- **Pruebas Automatizadas:** `src/business_logic/tests/LogicaUnitTest.php` cubre entrada válida, campos ausentes, UUID, límites de contador/valor, dominios específicos de O3 y temperatura, normalización y contrato de la consulta.
