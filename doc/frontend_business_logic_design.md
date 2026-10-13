# frontend_business_logic_design.md

## Diseño del Componente

**Componente:** `frontend_business_logic`
**Implementación:** `src/frontend_business_logic/`
**Lenguajes:** Java (cliente Android) y JavaScript (cliente web).

Este componente es el proxy/fake de lógica de negocio consumido por las interfaces gráficas. Presenta tipos y operaciones de dominio iguales a las del backend y encapsula por completo la comunicación remota.

### Tipos de dominio compartidos con `business_logic`

```text
MedidaEntrada = (
    uuid: Text,
    tipo_medida_id: N,
    valor: Z,
    contador: N,
    rssi: Z
)

Dispositivo = (
    dispositivo_id: Text,
    nombre: Text
)

TipoMedida = (
    tipo_medida_id: N,
    nombre: Text,
    unidad: Text
)

MedidaVista = (
    dispositivo_id: Text,
    dispositivo: Text,
    tipo_medida_id: N,
    tipo_medida: Text,
    unidad: Text,
    valor: Z,
    fecha_hora: Text
)

FiltrosMedida = (
    dispositivo_id: Text,
    tipo_medida_id: N,
    desde: Text,
    hasta: Text
)

EstadoBD = (
    ok: B,
    database: Text
)
```

### Interfaz pública del proxy

Las siguientes firmas son **idénticas** a sus homólogas de `business_logic_design.md`. Cada cliente implementa únicamente el subconjunto que utiliza.

```text
probarConexion() --> estado: EstadoBD

datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista

filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]

listarDispositivos() --> dispositivos: [Dispositivo]

listarTiposMedida() --> tipos: [TipoMedida]
```

### Subconjunto Android

```text
probarConexion() --> estado: EstadoBD

datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
```

La implementación Java está en `src/frontend_business_logic/android/`. La asincronía se implementa con callbacks, pero ese mecanismo se omite de las firmas lógicas conforme a la notación oficial.

### Subconjunto web

```text
probarConexion() --> estado: EstadoBD

filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]

listarDispositivos() --> dispositivos: [Dispositivo]

listarTiposMedida() --> tipos: [TipoMedida]
```

La implementación JavaScript está en `src/frontend_business_logic/web/LogicaFake.js`.

### Comunicación encapsulada

```text
GUI Android -> LogicaFake.java -> PeticionarioREST.java -> communication
GUI web     -> LogicaFake.js   -> pedir()              -> communication
```

`PeticionarioREST`, `fetch`, URLs, serialización y códigos de protocolo son detalles internos de este componente; las GUI no los utilizan directamente.

## Aclaraciones del Diseño

- La interfaz lógica coincide por nombre, parámetros y retorno con el subconjunto correspondiente de `business_logic_design.md`.
- `contador` y `rssi` pueden viajar en `MedidaEntrada` desde Android, aunque la persistencia simplificada no los almacena.
- `MedidaVista` ya no expone `medida_id`, `uuid`, `contador` ni `rssi`, porque esos campos no existen en la entidad persistida simplificada.
- Android compila los fuentes de este componente mediante `sourceSets` desde `src/android/app/build.gradle`, evitando duplicar clases dentro del componente GUI.
- El navegador carga `LogicaFake.js` desde este componente en desarrollo; el script de despliegue lo copia junto a la GUI sin crear una segunda copia versionada.

## Reglas Generales

- **Lenguaje de Programación:** Java 8 para la implementación Android y JavaScript sin frameworks para la implementación web.
- **Encabezados de Funciones/Métodos:** cada función o método propio debe incluir diseño lógico dentro de `--------------------` y una breve descripción.
- **Legibilidad del Código:** la interfaz pública usa nombres de dominio; todo detalle de transporte permanece interno al proxy.
- **Pruebas Automatizadas:** JUnit cubre la implementación Android y `src/frontend_business_logic/tests/web_proxy_test.js` verifica el proxy web y su aislamiento respecto de la GUI.
