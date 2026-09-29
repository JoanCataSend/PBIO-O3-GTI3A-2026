#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "EmisoraBLE.h"

class Publicador {

private:

  uint8_t beaconUUID[16] = {
    'E', 'P', 'S', 'G', '-', 'G', 'T', 'I',
    '-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
  };

  void publicar(uint8_t idMedida,
                int16_t valor,
                uint8_t contador,
                unsigned long tiempoEmisionMs) {

    // Major: [ID medida: 8 bits][contador: 8 bits]
    // Minor: valor de la medida
    const uint16_t major =
      ((uint16_t)idMedida << 8) | contador;

    const uint16_t minor =
      (uint16_t)valor;

    Serial.println();
    Serial.println("--- PUBLICACION iBeacon ---");

    Serial.print("ID medida = ");
    Serial.println(idMedida);

    Serial.print("contador = ");
    Serial.println(contador);

    Serial.print("major = ");
    Serial.println(major);

    Serial.print("minor = ");
    Serial.println(valor);

    laEmisora.emitirAnuncioIBeacon(
      beaconUUID,
      major,
      minor,
      RSSI
    );

    delay(tiempoEmisionMs);

    laEmisora.detenerAnuncio();
  }

public:

  EmisoraBLE laEmisora {
    "GTI3A-2025",
    0x004C,
    4
  };

  const int8_t RSSI = -53;

  enum MedicionesID {
    CO2 = 11,
    TEMPERATURA = 12,
    RUIDO = 13,
    O3 = 14
  };

  Publicador() {}

  void encenderEmisora() {
    laEmisora.encenderEmisora();
  }

  void publicarO3(int16_t valorPPB,
                  uint8_t contador,
                  unsigned long tiempoEmisionMs) {

    publicar(
      MedicionesID::O3,
      valorPPB,
      contador,
      tiempoEmisionMs
    );
  }

  void publicarTemperatura(int16_t temperaturaC,
                           uint8_t contador,
                           unsigned long tiempoEmisionMs) {

    publicar(
      MedicionesID::TEMPERATURA,
      temperaturaC,
      contador,
      tiempoEmisionMs
    );
  }
};

#endif
