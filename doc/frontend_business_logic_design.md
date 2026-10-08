# frontend_business_logic_design.md

## DiseÃ±o del Componente

**Componente:** `frontend_business_logic`
**ImplementaciÃ³n:** `src/frontend_business_logic/`
**Lenguajes:** Java (cliente Android) y JavaScript (cliente web).

Este componente es el proxy/fake de lÃ³gica de negocio consumido por las interfaces grÃ¡ficas. Presenta tipos y operaciones de dominio iguales a las del backend y encapsula por completo la comunicaciÃ³n remota.

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
```

### Interfaz pÃºblica del proxy

Las siguientes firmas son **idÃ©nticas** a sus homÃ³logas de `business_logic_design.md`. Cada cliente implementa Ãºnicamente el subconjunto que utiliza.

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

La implementaciÃ³n Java estÃ¡ en `src/frontend_business_logic/android/`. La asincronÃ­a se implementa con callbacks, pero ese mecanismo se omite de las firmas lÃ³gicas conforme a la notaciÃ³n oficial.

### Subconjunto web

```text
probarConexion() --> estado: EstadoBD

filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]

listarDispositivos() --> dispositivos: [Dispositivo]

listarTiposMedida() --> tipos: [TipoMedida]
```

La implementaciÃ³n JavaScript estÃ¡ en `src/frontend_business_logic/web/LogicaFake.js`.

### ComunicaciÃ³n encapsulada

```text
GUI Android -> LogicaFake.java -> PeticionarioREST.java -> communication
GUI web     -> LogicaFake.js   -> pedir()              -> communication
```

`PeticionarioREST`, `fetch`, URLs, serializaciÃ³n y cÃ³digos de protocolo son detalles internos de este componente; las GUI no los utilizan directamente.

## Aclaraciones del DiseÃ±o

- La interfaz lÃ³gica coincide por nombre, parÃ¡metros y retorno con el subconjunto correspondiente de `business_logic_design.md`.
- `MedidaEntrada`, `MedidaVista` y `EstadoBD` son tipos de dominio; la representaciÃ³n concreta recibida o enviada se convierte dentro del proxy.
- Android compila los fuentes de este componente mediante `sourceSets` desde `src/android/app/build.gradle`, evitando duplicar clases dentro del componente GUI.
- El navegador carga `LogicaFake.js` desde este componente en desarrollo; el script de despliegue lo copia junto a la GUI sin crear una segunda copia versionada.

## Reglas Generales

- **Lenguaje de ProgramaciÃ³n:** Java 8 para la implementaciÃ³n Android y JavaScript sin frameworks para la implementaciÃ³n web.
- **Encabezados de Funciones/MÃ©todos:** cada funciÃ³n o mÃ©todo propio debe incluir diseÃ±o lÃ³gico dentro de `--------------------` y una breve descripciÃ³n.
- **Legibilidad del CÃ³digo:** la interfaz pÃºblica usa nombres de dominio; todo detalle de transporte permanece interno al proxy.
- **Pruebas Automatizadas:** JUnit cubre la implementaciÃ³n Android y `src/frontend_business_logic/tests/web_proxy_test.js` verifica el proxy web y su aislamiento respecto de la GUI.
