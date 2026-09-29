// ============================================================
// NodoO3.ino
// ============================================================

#include <bluefruit.h>

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {
  Publicador elPublicador;
  Medidor elMedidor;
}

void setup() {

  Serial.begin(115200);
  delay(1500);

  Serial.println();
  Serial.println("======================================");
  Serial.println(" GTI Joan - NODO O3");
  Serial.println("======================================");

  Globales::elMedidor.iniciarMedidor();
  Globales::elPublicador.encenderEmisora();

  Serial.println();
  Serial.println("Sensor y Bluetooth preparados.");
  Serial.println("Vgas0 = Vref (Voffset = 0 mV)");
  Serial.println("---- setup(): fin ----");
}

void loop() {

  using namespace Globales;

  static uint8_t contador = 0;
  contador++;

  // 1) Medir con advertising detenido
  int16_t valorO3ppb =
    elMedidor.medirO3();

  // 2) Publicar O3
  elPublicador.publicarO3(
    valorO3ppb,
    contador,
    1200UL
  );

  delay(300);

  // 3) Medir temperatura
  int16_t temperaturaC =
    elMedidor.medirTemperatura();

  // 4) Publicar temperatura
  elPublicador.publicarTemperatura(
    temperaturaC,
    contador,
    1200UL
  );

  // 5) Espera
  delay(1500);
}
