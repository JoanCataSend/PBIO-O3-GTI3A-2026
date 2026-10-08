/*
 * Archivo: Publicador.h
 * Descripción: traduce las medidas del proyecto al protocolo iBeacon y coordina
 *              su publicación mediante EmisoraBLE.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
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

  EmisoraBLE laEmisora {
    "GTI Joan",
    0x004C,
    4
  };

  const int8_t RSSI_1M = -53;

  enum MedicionesID {
    TEMPERATURA = 12,
    O3 = 14
  };

  /*
   * --------------------
   * Diseño lógico:
   * id_medida: N, valor: Z, contador: N, tiempo_emision_ms: N --> publicar()
   * Descripción: codifica Major/Minor, inicia el anuncio iBeacon durante el
   * tiempo indicado y lo detiene al finalizar.
   * Precondición: 0 <= contador <= 255.
   * --------------------
   */
  void publicar(uint8_t idMedida,
                int32_t valor,
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
      RSSI_1M
    );

    delay(tiempoEmisionMs);

    laEmisora.detenerAnuncio();
  }

public:

  /*
   * --------------------
   * Diseño lógico: Publicador()
   * Descripción: construye el publicador con su emisora BLE configurada.
   * --------------------
   */
  Publicador() {}

  /*
   * --------------------
   * Diseño lógico: encenderEmisora()
   * Descripción: inicializa la emisora BLE asociada al publicador.
   * --------------------
   */
  void encenderEmisora() {
    laEmisora.encenderEmisora();
  }

  /*
   * --------------------
   * Diseño lógico:
   * valor_ppb: N, contador: N, tiempo_emision_ms: N --> publicarO3()
   * Descripción: publica una medida de O3 usando el ID lógico 14.
   * --------------------
   */
  void publicarO3(uint16_t valorPPB,
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
   * --------------------
   * Diseño lógico:
   * temperatura_c: Z, contador: N, tiempo_emision_ms: N --> publicarTemperatura()
   * Descripción: publica una temperatura usando el ID lógico 12.
   * --------------------
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
