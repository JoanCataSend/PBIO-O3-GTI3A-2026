/*
 * Archivo: Publicador.h
 * Descripción: traduce las medidas del proyecto al protocolo iBeacon y coordina
 *              su publicación mediante EmisoraBLE.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: definición de IDs O3/temperatura y codificación Major/Minor.
 */

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

#include "EmisoraBLE.h"

class Publicador {

private:

  uint8_t beaconUUID[16] = {
    'E', 'P', 'S', 'G', '-', 'G', 'T', 'I',
    '-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
  };

  /*
   * Diseño lógico:
   * idMedida:N, valor:Z, contador:N, tiempoEmisionMs:N --> publicar() -->
   * Descripción: codifica Major/Minor, inicia el anuncio iBeacon durante el
   *              tiempo indicado y lo detiene al finalizar.
   * Precondición: 0 <= contador <= 255.
   */
  void publicar(uint8_t idMedida,
                int16_t valor,
                uint8_t contador,
                unsigned long tiempoEmisionMs) {

    // Major: [ID medida: 8 bits][contador: 8 bits].
    // Minor: valor de la medida en complemento a dos cuando es negativo.
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
    "GTI Joan",
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

  /*
   * Diseño lógico: Publicador() -->
   * Descripción: construye el publicador con su emisora BLE configurada.
   */
  Publicador() {}

  /*
   * Diseño lógico: encenderEmisora() -->
   * Descripción: inicializa la emisora BLE asociada al publicador.
   */
  void encenderEmisora() {
    laEmisora.encenderEmisora();
  }

  /*
   * Diseño lógico:
   * valorPPB:Z, contador:N, tiempoEmisionMs:N --> publicarO3() -->
   * Descripción: publica una medida de O3 usando el ID lógico 14.
   */
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

  /*
   * Diseño lógico:
   * temperaturaC:Z, contador:N, tiempoEmisionMs:N --> publicarTemperatura() -->
   * Descripción: publica una temperatura usando el ID lógico 12.
   */
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
