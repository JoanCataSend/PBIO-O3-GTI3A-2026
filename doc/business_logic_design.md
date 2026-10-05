# business_logic_design.md

## Component Design (Diseño del Componente)

**Componente:** `business_logic`
**Implementación canónica para revisión:** `src/business_logic/`
**Código operativo equivalente:** `server/Logica.php`

La lógica de negocio valida entradas, resuelve entidades de catálogo, inserta medidas y proporciona consultas normalizadas. No decide códigos HTTP ni genera HTML.


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


### Operaciones públicas

```text
conexionBD() --> ConexionBD | Error
probarConexion() --> EstadoBD | Error
datos:MedidaEntrada --> insertarMedida() --> MedidaVista | Error
medidaId:N --> buscarMedidaConId() --> MedidaVista | Error
filtros:FiltrosMedida --> listarMedidas() --> [MedidaVista] | Error
listarDispositivos() --> [Dispositivo] | Error
listarTiposMedida() --> [TipoMedida] | Error
dispositivoId:N, tipoMedidaId:N --> buscarUltimaMedida() --> MedidaVista | Error
datos:MedidaEntrada --> validarMedidaEntrada() --> | Error
consultaMedidaVista() --> consulta:Texto
fila:MedidaVistaBD --> normalizarMedidaVista() --> MedidaVista
```

### Validación de `MedidaEntrada`

```text
uuid existe y no es vacío
tipoMedidaId es entero natural válido
valor es entero
contador es entero y 0 <= contador <= 255
rssi es entero
```

### Inserción

```text
validar entrada
buscar dispositivo por uuid
si no existe -> Error
buscar tipo por tipoMedidaId
si no existe -> Error
INSERT Medida(dispositivoId, tipoMedidaId, valor, contador, rssi)
recuperar la fila mediante consultaMedidaVista()
normalizar campos numéricos a enteros
devolver MedidaVista
```

### Separación de capas

```text
API REST --> business_logic --> MariaDB
```

La capa lógica lanza excepciones de dominio/validación; la capa HTTP decide cómo traducirlas a 400/404/500.

## Design Clarifications (Aclaraciones del Diseño)

- `SDBaseDatos.php` es configuración local privada y no forma parte del repositorio; se entrega `SDBaseDatos.example.php` sin credenciales reales.
- La lógica devuelve estructuras PHP equivalentes a las tuplas lógicas documentadas.
- `consultaMedidaVista()` centraliza los `JOIN` entre `Medida`, `Dispositivo` y `TipoMedida` para evitar duplicación.
- Los filtros de fecha y catálogo son opcionales y se añaden mediante consultas preparadas.

## General Rules (Reglas Generales)

- **Programming Language / Lenguaje de Programación:** PHP 8 con `declare(strict_types=1)` y extensión `mysqli`.
- **Function/Method Headers / Encabezados de Funciones/Métodos:** todas las funciones incluyen el diseño lógico en un bloque delimitado por `--------------------`.
- **Code Readability / Legibilidad del Código:** validación, persistencia, consulta y normalización se mantienen en funciones separadas y con nombres explícitos.
- **Automated Testing / Pruebas Automatizadas:** `src/business_logic/tests/LogicaUnitTest.php` cubre entradas válidas/erróneas, límites del contador, normalización y contrato de la consulta de vista.
- **Source correspondence / Correspondencia:** `src/business_logic/Logica.php` coincide con la lógica operativa de `server/Logica.php`.
