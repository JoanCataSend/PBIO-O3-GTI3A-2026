# Prompt · Firmware Sprint 0

```text
Genera firmware C++ completo para SparkFun Pro nRF52840 Mini usando Bluefruit52Lib. Necesito exactamente cuatro archivos: firmware/NodoO3/NodoO3.ino, Medidor.h, Publicador.h y EmisoraBLE.h.

Sprint 0: NO leas sensores ni ADC. Medidor.medirO3() devuelve siempre 123 ppb y medirTemperatura() siempre -12 °C.

BLE: nombre "GTI Joan", UUID de 16 bytes ASCII "EPSG-GTI-PROY-3A", manufacturer 0x004C, anuncio iBeacon no conectable y escaneable, intervalo 100 ms.

IDs: CO2=11, TEMPERATURA=12, RUIDO=13, O3=14.
Major=(idMedida<<8)|contador. Minor=valor de 16 bits, preservando complemento a dos para negativos.

Ciclo: contador++, publicar O3 1200 ms, pausa 300 ms, publicar temperatura 1200 ms, pausa 1500 ms.

Mantén clases Medidor, Publicador y EmisoraBLE. Añade cabecera de fichero con nombre, descripción, copyright 2026 Joan (uso académico PBIO - UPV), fecha 2026-10-01, autor y aportación. Encima de cada función incluye diseño lógico en formato entrada:Tipo --> funcion() --> salida:Tipo y breve descripción. Tipos lógicos: N natural, Z entero, Texto texto, [N]_16 array fijo.

Entrega archivos completos, compilables, sin fragmentos ni TODOs.
```
