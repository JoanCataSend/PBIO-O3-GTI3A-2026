PBIO O3 - REVISION FINAL

SENSOR INDIVIDUAL
-----------------
Data Matrix leído:
032824010609 110406 O3 2404 -74.04

Interpretación:
- Número de serie: 032824010609
- Part number: 110406
- Gas: O3
- Fecha de test: 2404
- Sensibilidad individual: -74.04 nA/ppm

Módulo ULPSM-O3:
- BIAS: -25 mV (polarización interna, NO es Voffset)
- TIA Gain: 499 kV/A

Factor de conversión individual:
M = -74.04 * 499 * 10^-6
M = -0.03694596 V/ppm
M = -36.94596 mV/ppm

IMPORTANTE SOBRE EL CERO
------------------------
El Data Matrix NO contiene Vgas0 ni Voffset.

La versión actual usa:
Voffset = 0
Vgas0 = Vref

Esto es una aproximación inicial válida, pero NO equivale a una
calibración absoluta en aire limpio.

Para mayor precisión, el fabricante indica que hay que estabilizar
el sensor en aire limpio libre de O3 y guardar Vgas como Vgas0.

El filtro (mediana + EMA) estabiliza la visualización, pero NO mejora
por sí solo la exactitud absoluta de la calibración.
