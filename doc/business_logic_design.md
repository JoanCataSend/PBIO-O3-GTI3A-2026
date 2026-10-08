# business_logic_design.md

## Diseño del Componente

**Componente:** `business_logic`  
**Implementación:** `src/business_logic/`  
**Lenguaje:** PHP 8.

Este componente contiene las reglas de dominio y el acceso a persistencia. Su interfaz utiliza únicamente tipos abstractos de la notación oficial y entidades alineadas con `database_design.md`.

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

EstadoBD = (
    ok: B,
    database: Text
)

ConexionBD = (
    activa: B
)
```

Los campos de `FiltrosMedida` son criterios opcionales: un criterio ausente en la llamada no se aplica. Esta ausencia es una condición de la operación, no un tipo adicional de la notación.

### Interfaz de lógica de negocio

```text
probarConexion() --> estado: EstadoBD

datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista

medida_id: N --> buscarMedidaConId() --> medida: MedidaVista

filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]

listarDispositivos() --> dispositivos: [Dispositivo]

listarTiposMedida() --> tipos: [TipoMedida]

dispositivo_id: N, tipo_medida_id: N --> buscarUltimaMedida() --> medida: MedidaVista

datos: MedidaEntrada --> validarMedidaEntrada()
```

### Operaciones internas de persistencia

```text
conexionBD() --> conexion: ConexionBD

consultaMedidaVista() --> consulta: Text

fila: MedidaVista --> normalizarMedidaVista() --> medida: MedidaVista
```

### Alineación explícita con `database_design.md`

```text
Dispositivo:
    dispositivo_id <-> Dispositivo.dispositivoId
    uuid           <-> Dispositivo.uuid
    nombre         <-> Dispositivo.nombre

TipoMedida:
    tipo_medida_id <-> TipoMedida.tipoMedidaId
    nombre         <-> TipoMedida.nombre
    unidad         <-> TipoMedida.unidad

MedidaVista:
    medida_id      <-> Medida.medidaId
    dispositivo_id <-> Medida.dispositivoId
    tipo_medida_id <-> Medida.tipoMedidaId
    valor          <-> Medida.valor
    contador       <-> Medida.contador
    rssi           <-> Medida.rssi
    fecha_hora     <-> Medida.fechaHora

Los campos uuid, dispositivo, tipo_medida y unidad de MedidaVista
se obtienen relacionando Medida con Dispositivo y TipoMedida.
```

### Reglas de validación

```text
uuid tiene exactamente 16 caracteres
0 <= tipo_medida_id <= 255
0 <= contador <= 255
rssi pertenece a Z
si tipo_medida_id = 14 entonces 0 <= valor <= 65535
si tipo_medida_id = 12 entonces -32768 <= valor <= 32767
el dispositivo debe existir en Dispositivo
el tipo debe existir en TipoMedida
```

### Inserción

```text
validarMedidaEntrada(datos)
resolver Dispositivo por uuid
resolver TipoMedida por tipo_medida_id
insertar la fila en Medida
recuperar la fila creada
producir MedidaVista
```

## Aclaraciones del Diseño

- Los nombres lógicos usan `snake_case`; PHP y SQL conservan los identificadores implementados en `camelCase`. La correspondencia se declara arriba de forma explícita.
- `ConexionBD` abstrae el recurso de persistencia; el diseño no expresa handles, referencias ni objetos concretos del lenguaje.
- Las filas leídas de la base se normalizan a `N` y `Z` antes de formar los tipos de dominio.
- Los filtros opcionales se validan antes de usarse y las consultas variables se construyen mediante parámetros enlazados.
- `SDBaseDatos.php` es configuración privada local y no se versiona; el repositorio solo contiene `SDBaseDatos.example.php`.

## Reglas Generales

- **Lenguaje de Programación:** PHP 8 con `declare(strict_types=1)` y `mysqli` para persistencia.
- **Encabezados de Funciones/Métodos:** cada función debe incluir su diseño lógico dentro de un bloque `--------------------` y una breve descripción.
- **Legibilidad del Código:** mantener separadas validación, consulta, persistencia y normalización; la capa debe depender únicamente del dominio y de la persistencia definida en `database_design.md`.
- **Pruebas Automatizadas:** `src/business_logic/tests/LogicaUnitTest.php` cubre entradas válidas e inválidas, límites por tipo, normalización y contrato de consulta.
