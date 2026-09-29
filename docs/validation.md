# Validación

## 1. Alimentación

Comprobar con multímetro:

```text
3V3 ↔ GND ≈ 3.3 V
```

## 2. Vref

Comparar el valor del Monitor Serie con un multímetro.

Debe ser aproximadamente:

```text
Vref ≈ V+/2
```

## 3. Vgas

Comparar Monitor Serie y multímetro para descartar errores grandes de adquisición.

## 4. Temperatura

Comparar con un termómetro externo tras dejar estabilizar el conjunto.

## 5. Cálculo de O₃

Comprobar manualmente:

```text
DeltaV = Vgas - Vgas0
O3_RAW = DeltaV / (-0.03694596)
```

y comparar con el Monitor Serie.

## 6. Filtro

`O3_FILTRADO` debe variar más suavemente que `O3_CORR`.

## 7. iBeacon

Ejemplo O₃:

```text
ID = 14
contador = 16
Minor = 291
Major = 3600
```

Android debe mostrar el mismo `Minor`.

## 8. Fuente de O₃

Antes, durante y después de la exposición registrar:

- `Vgas`
- `Vref`
- `O3 RAW`
- `O3 CORR`
- `O3 FILTRADO`

La prueba permite comprobar respuesta funcional del sensor. La concentración exacta solo puede validarse frente a una referencia conocida.

## 9. Estabilidad

Mantener el sistema funcionando 20–30 minutos y comprobar:

- contador avanzando;
- alternancia de ID 14 e ID 12;
- recepción continuada;
- ausencia de bloqueos;
- estabilidad razonable de `Vref`.
