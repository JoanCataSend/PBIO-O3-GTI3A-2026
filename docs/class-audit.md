# Auditoría de clases respecto a la ingeniería inversa

## Conservadas / usadas

### Firmware

- `Medidor`
- `Publicador`
- `EmisoraBLE`

### Android

- `MainActivity`
- `TramaIBeacon`
- `Utilidades`

## Añadidas para completar el flujo real

### Android

- `MedidaEntrada`
- `LogicaFake`
- `PeticionarioREST`

## Clases del código original que no se reintroducen

La ingeniería inversa anterior describía también clases auxiliares como
`LED`, `PuertoSerie`, `ServicioEnEmisora` y `Caracteristica`.

No se añaden como código muerto porque el prototipo actual:

- no necesita encapsular el LED para cumplir el flujo de la práctica;
- usa `Serial` directamente de forma suficiente;
- publica iBeacon mediante advertising no conectable;
- no expone un servicio GATT ni características BLE conectables.

La responsabilidad actual queda cubierta por las clases que realmente
participan en la ejecución.
