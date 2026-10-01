// ============================================================
// Medidor.h
//
// Descripción:
// Clase encargada de proporcionar las medidas utilizadas por
// el nodo durante el Sprint 0.
//
// En esta versión las medidas son ficticias y constantes.
// No se accede todavía al sensor físico de O3.
//
// Esta implementación permite comprobar el funcionamiento
// completo del sistema:
//
// Arduino -> BLE -> Android -> API REST -> BBDD -> Web
//
// Autor: Joan
// Fecha: 2026
// Aportación:
// Adaptación del Medidor proporcionado por el profesor para
// trabajar con la magnitud O3 del proyecto PBIO.
//
// ============================================================

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO

#include <Arduino.h>


class Medidor {

public:

  /*
   * Medidor()
   *
   * Constructor de la clase.
   * En el Sprint 0 no es necesario inicializar hardware,
   * ya que las medidas utilizadas son ficticias.
   */
  Medidor() {
  }


  /*
   * iniciarMedidor()
   *
   * Inicializa el medidor.
   *
   * En esta versión fake no se configura ningún ADC ni
   * ningún sensor físico. Únicamente informa por puerto
   * serie de que se están utilizando valores ficticios.
   */
  void iniciarMedidor() {

    Serial.println();
    Serial.println("Medidor iniciado - SPRINT 0");
    Serial.println("Modo: medidas ficticias");
    Serial.println("O3 fijo = 123 ppb");
    Serial.println("Temperatura fija = -12 C");
  }


  /*
   * medirO3()
   *
   * Devuelve una medida ficticia de concentración de O3.
   *
   * Se utiliza el valor 123 siguiendo el mismo criterio
   * del código proporcionado por el profesor, donde
   * medirCO2() devolvía siempre el valor 123.
   *
   * Retorno:
   *   Concentración ficticia de O3 en ppb.
   */
  int16_t medirO3() {

    const int16_t valorO3 = 123;

    Serial.println();
    Serial.println("--- MEDIDA FICTICIA O3 ---");
    Serial.print("O3 = ");
    Serial.print(valorO3);
    Serial.println(" ppb");

    return valorO3;
  }


  /*
   * medirTemperatura()
   *
   * Devuelve una temperatura ficticia.
   *
   * Se conserva el valor -12 utilizado en el ejemplo
   * original proporcionado por el profesor.
   *
   * Retorno:
   *   Temperatura ficticia en grados Celsius.
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