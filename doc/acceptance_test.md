# Test de aceptación presencial · Sprint 0

## Objetivo

Demostrar de forma reproducible el criterio obligatorio de la rúbrica: una medida ficticia definida en Arduino debe atravesar **firmware → BLE/iBeacon → Android → REST → lógica de negocio → MariaDB → web** y aparecer en el navegador con el mismo valor.

## Preparación

- SparkFun Pro nRF52840 Mini con el firmware de `src/firmware/NodoO3/`.
- Teléfono Android físico con BLE y la app de `src/android/` instalada.
- Backend/API y MariaDB desplegados y accesibles.
- Navegador abierto en la web desplegada.
- Monitor Serie a 115200 baudios.
- Rúbrica impresa, tal como pide el enunciado.

## Procedimiento reproducible

1. En `src/firmware/NodoO3/Medidor.h`, cambiar `valorO3` a un valor fácil de reconocer, por ejemplo `321`.
2. Compilar/cargar el firmware y reiniciar la placa.
3. En Monitor Serie, comprobar `O3 = 321 ppb` y la publicación con ID 14.
4. En Android, pulsar **Buscar GTI Joan**.
5. Verificar que la app muestra `O₃: 321 ppb`, un contador y RSSI, y finalmente `Servidor: medida guardada`.
6. En la web, pulsar **Actualizar** o esperar al refresco automático.
7. Verificar que la tarjeta/tabla contiene O3 = `321 ppb` para `GTI Joan`.
8. Si el profesor lo pide, consultar la API o MariaDB y mostrar la misma fila almacenada.

## Resultado esperado

```text
valor_firmware = valor_android = valor_bbdd = valor_web
```

El test solo se considera superado si la medida nueva introducida al comienzo es la que termina mostrándose en la web.

## Comprobaciones rápidas si algo falla

- **No aparece BLE:** comprobar permisos, Bluetooth, nombre `GTI Joan`, UUID `EPSG-GTI-PROY-3A` y usar teléfono físico.
- **Android recibe pero no guarda:** revisar el texto de estado del servidor, conectividad HTTPS y URL configurada en `LogicaFake.URL_API`.
- **API devuelve error:** probar `?accion=health`, comprobar `SDBaseDatos.php` y que dispositivo/tipos 12 y 14 estén sembrados.
- **Web no actualiza:** comprobar `api.php?accion=medidas`, consola del navegador y que `LogicaFake.js` use la ruta relativa `api.php`.

## Defensa de arquitectura

Si se pregunta por segregación de responsabilidades:

- `Medidor` mide/genera; no conoce BLE.
- `Publicador` codifica el protocolo; no mide.
- `EmisoraBLE` conoce Bluefruit; no conoce negocio.
- `MainActivity` coordina BLE/UI; HTTP queda en `PeticionarioREST` detrás de `LogicaFake`.
- `business_logic` valida y accede a BBDD; no conoce HTTP.
- `api_rest` adapta HTTP/JSON; no contiene SQL.
- `LogicaFake.js` conoce REST; `app.js` conoce la interfaz.
