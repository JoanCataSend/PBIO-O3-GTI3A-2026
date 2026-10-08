/*
 * Archivo: EmisoraBLE.h
 * Descripción: encapsula la configuración y publicación BLE/iBeacon del nodo.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: adaptación de la emisora BLE del código base al nodo GTI Joan.
 */

#ifndef EMISORABLE_H_INCLUIDO
#define EMISORABLE_H_INCLUIDO

#include <bluefruit.h>

class EmisoraBLE {

private:

  const char* nombre;
  uint16_t fabricanteID;
  int8_t potenciaRadio;

public:

  /*
   * --------------------
   * Diseño lógico:
   * nombre_emisora: Text, fabricante: N, tx_power: Z --> EmisoraBLE()
   * Descripción: construye una emisora con nombre, fabricante y potencia.
   * --------------------
   */
  EmisoraBLE(const char* nombreEmisora,
             uint16_t fabricante,
             int8_t txPower)
    : nombre(nombreEmisora),
      fabricanteID(fabricante),
      potenciaRadio(txPower) {}

  /*
   * --------------------
   * Diseño lógico: encenderEmisora()
   * Descripción: inicializa Bluefruit y deja el advertising detenido.
   * --------------------
   */
  void encenderEmisora() {

    Bluefruit.begin();
    Bluefruit.setName(nombre);
    Bluefruit.setTxPower(potenciaRadio);

    detenerAnuncio();
  }

  /*
   * --------------------
   * Diseño lógico:
   * uuid: [N]_16, major: N, minor: N, rssi_1m: Z --> emitirAnuncioIBeacon()
   * Descripción: configura y comienza un anuncio iBeacon no conectable.
   * --------------------
   */
  void emitirAnuncioIBeacon(const uint8_t uuid[16],
                            uint16_t major,
                            uint16_t minor,
                            int8_t rssi1m) {

    detenerAnuncio();

    Bluefruit.Advertising.clearData();
    Bluefruit.ScanResponse.clearData();

    Bluefruit.Advertising.addFlags(
      BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE
    );

    BLEBeacon beacon(
      uuid,
      major,
      minor,
      rssi1m
    );

    beacon.setManufacturer(
      fabricanteID
    );

    Bluefruit.Advertising.setBeacon(
      beacon
    );

    // El nombre se publica en la scan response.
    Bluefruit.ScanResponse.addName();

    // Anuncio no conectable y escaneable.
    Bluefruit.Advertising.setType(
      BLE_GAP_ADV_TYPE_NONCONNECTABLE_SCANNABLE_UNDIRECTED
    );

    // 160 * 0.625 ms = 100 ms.
    Bluefruit.Advertising.setInterval(
      160,
      160
    );

    Bluefruit.Advertising.setFastTimeout(
      30
    );

    Bluefruit.Advertising.start(
      0
    );
  }

  /*
   * --------------------
   * Diseño lógico: detenerAnuncio()
   * Descripción: detiene el advertising si se encuentra activo.
   * --------------------
   */
  void detenerAnuncio() {

    if (Bluefruit.Advertising.isRunning()) {
      Bluefruit.Advertising.stop();
    }
  }
};

#endif
