#ifndef EMISORABLE_H_INCLUIDO
#define EMISORABLE_H_INCLUIDO

#include <bluefruit.h>

class EmisoraBLE {

private:

  const char* nombre;
  uint16_t fabricanteID;
  int8_t potenciaRadio;

public:

  EmisoraBLE(const char* nombreEmisora,
             uint16_t fabricante,
             int8_t txPower)
    : nombre(nombreEmisora),
      fabricanteID(fabricante),
      potenciaRadio(txPower) {}

  void encenderEmisora() {

    Bluefruit.begin();
    Bluefruit.setName(nombre);
    Bluefruit.setTxPower(potenciaRadio);

    detenerAnuncio();
  }

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

    // Nombre en scan response
    Bluefruit.ScanResponse.addName();

    // No conectable, escaneable
    Bluefruit.Advertising.setType(
      BLE_GAP_ADV_TYPE_NONCONNECTABLE_SCANNABLE_UNDIRECTED
    );

    // 160 * 0.625 ms = 100 ms
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

  void detenerAnuncio() {

    if (Bluefruit.Advertising.isRunning()) {
      Bluefruit.Advertising.stop();
    }
  }
};

#endif
