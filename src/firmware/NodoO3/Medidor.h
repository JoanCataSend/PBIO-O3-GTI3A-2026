/*
 * Archivo: Medidor.h
 * Descripción: clase que proporciona las medidas ficticias del Sprint 0.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: adaptación del Medidor del código proporcionado por el profesor
 *             para trabajar con O3 y temperatura en el proyecto PBIO.
 */

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

#include <Arduino.h>

class Medidor {

public:

  /*
   * --------------------
   * Diseño lógico: Medidor() -->
   * Descripción: construye el medidor ficticio. No inicializa hardware.
   * --------------------
   */
  Medidor() {
  }

  /*
   * --------------------
   * Diseño lógico: iniciarMedidor() -->
   * Descripción: informa por puerto serie de los valores ficticios usados.
   * --------------------
   */
  void iniciarMedidor() {

    Serial.println();
    Serial.println("Medidor iniciado - SPRINT 0");
    Serial.println("Modo: medidas ficticias");
    Serial.println("O3 fijo = 123 ppb");
    Serial.println("Temperatura fija = -12 C");
  }

  /*
   * --------------------
   * Diseño lógico: medirO3() --> valor_o3: N
   * Descripción: devuelve la concentración ficticia de O3 del Sprint 0.
   * Postcondición: valorO3 = 123 ppb.
   * --------------------
   */
  uint16_t medirO3() {

    const uint16_t valorO3 = 123;

    Serial.println();
    Serial.println("--- MEDIDA FICTICIA O3 ---");
    Serial.print("O3 = ");
    Serial.print(valorO3);
    Serial.println(" ppb");

    return valorO3;
  }

  /*
   * --------------------
   * Diseño lógico: medirTemperatura() --> temperatura: Z
   * Descripción: devuelve la temperatura ficticia del Sprint 0.
   * Postcondición: temperatura = -12 grados Celsius.
   * --------------------
   */
  int16_t medirTemperatura() {

    const int16_t valorTemperatura = -12;

    Serial.println();
    Serial.println("--- MEDIDA FICTICIA TEMPERATURA ---");
    Serial.print("Temperatura = ");
    Serial.print(valorTemperatura);
    Serial.println(" C");

    return valorTemperatura;
  }
};

#endif
