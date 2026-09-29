// ============================================================
// Medidor.h
// Sensor concreto: SPEC 110406 O3
// Data Matrix: 032824010609 110406 O3 2404 -74.04
// Sensibilidad individual: -74.04 nA/ppm
// ULPSM-O3: TIA Gain = 499 kV/A
// M = -0.03694596 V/ppm
// ============================================================

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

#include <Arduino.h>

class Medidor {

private:

  static const uint8_t PIN_VGAS  = 5;
  static const uint8_t PIN_VREF  = 28;
  static const uint8_t PIN_VTEMP = 29;

  const float ADC_REF_V = 3.6f;
  const float ADC_MAX   = 4095.0f;
  static const int NUM_MUESTRAS_ADC = 50;

  // Datos INDIVIDUALES del sensor
  const float SENSIBILIDAD_NA_PPM = -74.04f;
  const float TIA_GAIN_KV_A = 499.0f;
  const float M_V_PPM = -0.03694596f;

  // El código Data Matrix NO proporciona Voffset.
  // El fabricante permite comenzar con Voffset = 0.
  const float V_OFFSET = 0.0f;

  // Filtro: mediana de 5 + exponencial
  static const int TAM_FILTRO = 5;
  float historialO3[TAM_FILTRO];
  int indiceO3 = 0;
  int muestrasO3 = 0;
  float o3Filtrado = 0.0f;
  bool filtroInicializado = false;
  const float ALPHA_O3 = 0.25f;

  struct Lectura {
    float vgas;
    float vref;
    float vtemp;
    float vplus;
    float temperatura;
  };

  float leerVoltaje(uint8_t pin) {

    uint32_t suma = 0;

    // Descartar primera lectura tras cambio de canal
    analogRead(pin);
    delayMicroseconds(300);

    for (int i = 0; i < NUM_MUESTRAS_ADC; i++) {
      suma += analogRead(pin);
      delay(2);
    }

    const float mediaADC =
      (float)suma / (float)NUM_MUESTRAS_ADC;

    return mediaADC * ADC_REF_V / ADC_MAX;
  }

  Lectura leer() {

    Lectura l;

    l.vgas  = leerVoltaje(PIN_VGAS);
    l.vref  = leerVoltaje(PIN_VREF);
    l.vtemp = leerVoltaje(PIN_VTEMP);

    // Vref ~ V+/2
    l.vplus = 2.0f * l.vref;

    // T = (87/V+) * Vtemp - 18
    if (l.vplus > 0.1f) {
      l.temperatura =
        (87.0f / l.vplus) * l.vtemp - 18.0f;
    } else {
      l.temperatura = 0.0f;
    }

    return l;
  }

  // Coeficiente típico del datasheet:
  // -20..30 C -> 0 ppm/C
  // 30..50 C  -> 0.0066 ppm/C
  float zeroShiftTemperatura(float t) {

    if (t < -20.0f) t = -20.0f;
    if (t >  50.0f) t =  50.0f;

    if (t <= 30.0f) {
      return 0.0f;
    }

    return 0.0066f * (t - 30.0f);
  }

  // Coeficiente típico de span: 0.3 %/C respecto a 20 C
  float factorSpanTemperatura(float t) {

    if (t < -20.0f) t = -20.0f;
    if (t >  50.0f) t =  50.0f;

    return 1.0f + 0.003f * (t - 20.0f);
  }

  float calcularMediana(float datos[], int n) {

    float copia[TAM_FILTRO];

    for (int i = 0; i < n; i++) {
      copia[i] = datos[i];
    }

    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        if (copia[j] < copia[i]) {
          float aux = copia[i];
          copia[i] = copia[j];
          copia[j] = aux;
        }
      }
    }

    if (n % 2 == 1) {
      return copia[n / 2];
    }

    return (copia[n / 2 - 1] + copia[n / 2]) / 2.0f;
  }

  float filtrarO3(float ppbNuevo) {

    historialO3[indiceO3] = ppbNuevo;

    indiceO3++;
    if (indiceO3 >= TAM_FILTRO) {
      indiceO3 = 0;
    }

    if (muestrasO3 < TAM_FILTRO) {
      muestrasO3++;
    }

    const float mediana =
      calcularMediana(historialO3, muestrasO3);

    if (!filtroInicializado) {
      o3Filtrado = mediana;
      filtroInicializado = true;
    } else {
      o3Filtrado =
        ALPHA_O3 * mediana +
        (1.0f - ALPHA_O3) * o3Filtrado;
    }

    return o3Filtrado;
  }

public:

  Medidor() {
    for (int i = 0; i < TAM_FILTRO; i++) {
      historialO3[i] = 0.0f;
    }
  }

  void iniciarMedidor() {

    pinMode(PIN_VGAS, INPUT);
    pinMode(PIN_VREF, INPUT);
    pinMode(PIN_VTEMP, INPUT);

    analogReference(AR_DEFAULT);
    analogReadResolution(12);
    analogSampleTime(40);
    analogOversampling(16);
    analogCalibrateOffset();

    Serial.println();
    Serial.println("Medidor ULPSM-O3 iniciado");
    Serial.println("Sensor individual:");
    Serial.println("  Serial: 032824010609");
    Serial.println("  Part number: 110406");
    Serial.println("  Gas: O3");
    Serial.println("  Fecha test: 2404");
    Serial.println("  Sensibilidad: -74.04 nA/ppm");
    Serial.println("  TIA Gain: 499 kV/A");
    Serial.println("  M: -0.03694596 V/ppm");
    Serial.println("Voffset = 0 mV -> Vgas0 = Vref");
    Serial.println("Filtro O3: mediana 5 + EMA alpha 0.25");
  }

  int16_t medirO3() {

    const Lectura l = leer();

    // Vgas0 = Vref + Voffset; Voffset = 0
    const float vgas0 = l.vref + V_OFFSET;
    const float deltaV = l.vgas - vgas0;

    // C = (Vgas - Vgas0) / M
    const float ppmRaw = deltaV / M_V_PPM;

    // Compensación típica de temperatura:
    // primero cero y luego span
    const float cambioZero =
      zeroShiftTemperatura(l.temperatura);

    const float span =
      factorSpanTemperatura(l.temperatura);

    const float ppmCorregido =
      (ppmRaw - cambioZero) / span;

    const float ppbInstantaneo =
      ppmCorregido * 1000.0f;

    float ppbFiltrado =
      filtrarO3(ppbInstantaneo);

    Serial.println();
    Serial.println("--- MEDIDA O3 ---");

    Serial.print("Vgas = ");
    Serial.print(l.vgas, 5);
    Serial.println(" V");

    Serial.print("Vref = ");
    Serial.print(l.vref, 5);
    Serial.println(" V");

    Serial.print("Vgas0 = ");
    Serial.print(vgas0, 5);
    Serial.println(" V");

    Serial.print("DeltaV = ");
    Serial.print(deltaV * 1000.0f, 3);
    Serial.println(" mV");

    Serial.print("Temperatura usada = ");
    Serial.print(l.temperatura, 2);
    Serial.println(" C");

    Serial.print("O3 RAW = ");
    Serial.print(ppmRaw * 1000.0f, 1);
    Serial.println(" ppb");

    Serial.print("O3 CORR = ");
    Serial.print(ppbInstantaneo, 1);
    Serial.println(" ppb");

    Serial.print("O3 FILTRADO = ");
    Serial.print(ppbFiltrado, 1);
    Serial.println(" ppb");

    // El valor transmitido no puede ser negativo
    if (ppbFiltrado < 0.0f) {
      ppbFiltrado = 0.0f;
    }

    // Rango usado para transmisión
    if (ppbFiltrado > 20000.0f) {
      ppbFiltrado = 20000.0f;
    }

    long ppb = lroundf(ppbFiltrado);

    if (ppb < 0) ppb = 0;
    if (ppb > 20000) ppb = 20000;

    return (int16_t)ppb;
  }

  int16_t medirTemperatura() {

    const Lectura l = leer();

    int temperatura =
      (int)lroundf(l.temperatura);

    if (temperatura < -32768) temperatura = -32768;
    if (temperatura >  32767) temperatura =  32767;

    Serial.print("Temperatura = ");
    Serial.print(l.temperatura, 2);
    Serial.print(" C -> ");
    Serial.println(temperatura);

    return (int16_t)temperatura;
  }
};

#endif
