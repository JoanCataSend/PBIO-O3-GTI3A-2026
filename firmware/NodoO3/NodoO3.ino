/*
 * Archivo: NodoO3.ino
 * Descripción: programa principal del nodo BLE del Sprint 0. Coordina la
 *              obtención de medidas ficticias y su publicación como iBeacon.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: adaptación del código base del profesor al nodo O3 del proyecto.
 */

#include <bluefruit.h>

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"

namespace Globales {
  Publicador elPublicador;
  Medidor elMedidor;
}

/*
 * Diseño lógico: setup() --> sistemaInicializado:VoF
 * Descripción: inicializa el puerto serie, el medidor ficticio y la emisora BLE.
 * Nota: Arduino no devuelve explícitamente el booleano; el diseño representa
 *       el estado lógico alcanzado al finalizar correctamente.
 */
void setup() {

  Serial.begin(115200);
  delay(1500);

  Serial.println();
  Serial.println("======================================");
  Serial.println(" GTI Joan - NODO O3 - SPRINT 0");
  Serial.println("======================================");

  Globales::elMedidor.iniciarMedidor();
  Globales::elPublicador.encenderEmisora();

  Serial.println();
  Serial.println("Medidor ficticio y Bluetooth preparados.");
  Serial.println("---- setup(): fin ----");
}

/*
 * Diseño lógico: loop() -->
 * Descripción: genera un contador, obtiene O3 y temperatura ficticios y los
 *              publica secuencialmente mediante iBeacon.
 * Precondición: setup() ha finalizado correctamente.
 */
void loop() {

  using namespace Globales;

  static uint8_t contador = 0;
  contador++;

  // 1) Obtener medida ficticia de O3.
  int16_t valorO3ppb =
    elMedidor.medirO3();

  // 2) Publicar O3.
  elPublicador.publicarO3(
    valorO3ppb,
    contador,
    1200UL
  );

  delay(300);

  // 3) Obtener medida ficticia de temperatura.
  int16_t temperaturaC =
    elMedidor.medirTemperatura();

  // 4) Publicar temperatura.
  elPublicador.publicarTemperatura(
    temperaturaC,
    contador,
    1200UL
  );

  // 5) Espera antes del siguiente ciclo.
  delay(1500);
}
