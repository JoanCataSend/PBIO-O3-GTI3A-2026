/*
 * Archivo: ProtocolUnitTest.java
 * Descripción: tests unitarios del protocolo Major/Minor usado entre firmware y Android.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verificación automática de codificación/decodificación del protocolo PBIO.
 */

package es.upv.jcatsen.pbio;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProtocolUnitTest {

    /**
     * --------------------
     * Diseño lógico: majorContieneIdMedidaYContador() -->
     * Descripción: prueba automáticamente el criterio indicado.
     * Criterio: ID 14 y contador 16 deben producir Major 3600 y ser reversibles.
     * --------------------
     */
    @Test
    public void majorContieneIdMedidaYContador() {
        int measurementId = 14;
        int counter = 16;

        int major = (measurementId << 8) | counter;

        assertEquals(3600, major);
        assertEquals(14, (major >> 8) & 0xFF);
        assertEquals(16, major & 0xFF);
    }

    /**
     * --------------------
     * Diseño lógico: tramaCompletaIBeaconSeAnalizaCorrectamente() -->
     * Descripción: construye un advertising completo y verifica prefijo, UUID, Major, Minor y TxPower.
     * Criterio: una trama PBIO realista debe quedar marcada como válida y conservar todos sus campos.
     * --------------------
     */
    @Test
    public void tramaCompletaIBeaconSeAnalizaCorrectamente() {
        byte[] uuid = "EPSG-GTI-PROY-3A".getBytes();
        byte[] anuncio = new byte[] {
                0x02, 0x01, 0x06,
                0x1A, (byte) 0xFF,
                0x4C, 0x00, 0x02, 0x15,
                uuid[0], uuid[1], uuid[2], uuid[3],
                uuid[4], uuid[5], uuid[6], uuid[7],
                uuid[8], uuid[9], uuid[10], uuid[11],
                uuid[12], uuid[13], uuid[14], uuid[15],
                0x0E, 0x10,
                0x00, 0x7B,
                (byte) 0xB7
        };

        TramaIBeacon trama = new TramaIBeacon(anuncio);

        assertTrue(trama.esValida());
        assertArrayEquals(uuid, trama.getUUID());
        assertEquals(3600, Utilidades.bytesToUnsignedInt(trama.getMajor()));
        assertEquals(123, Utilidades.bytesToUnsignedInt(trama.getMinor()));
        assertEquals(-73, trama.getTxPower());
    }

    /**
     * --------------------
     * Diseño lógico: tramaSinPrefijoIBeaconSeRechaza() -->
     * Descripción: verifica que bytes BLE sin 4C 00 02 15 no se acepten como iBeacon.
     * --------------------
     */
    @Test
    public void tramaSinPrefijoIBeaconSeRechaza() {
        TramaIBeacon trama = new TramaIBeacon(
                new byte[] {0x02, 0x01, 0x06, 0x01, 0x02, 0x03}
        );

        assertFalse(trama.esValida());
    }

    /**
     * --------------------
     * Diseño lógico: minorConSignoDecodificaTemperaturaSprint0() -->
     * Descripción: prueba automáticamente la decodificación del valor ficticio.
     * Criterio Sprint 0: FF F4 debe decodificarse como -12 °C.
     * --------------------
     */
    @Test
    public void minorConSignoDecodificaTemperaturaSprint0() {
        byte[] minor = new byte[] {
                (byte) 0xFF,
                (byte) 0xF4
        };

        assertEquals(
                -12,
                Utilidades.bytesToSignedInt16(minor)
        );
    }

    /**
     * --------------------
     * Diseño lógico: minorSinSignoDecodificaO3Sprint0() -->
     * Descripción: prueba automáticamente la decodificación del valor ficticio.
     * Criterio Sprint 0: 00 7B debe decodificarse como 123 ppb.
     * --------------------
     */
    @Test
    public void minorSinSignoDecodificaO3Sprint0() {
        byte[] minor = new byte[] {
                (byte) 0x00,
                (byte) 0x7B
        };

        assertEquals(
                123,
                Utilidades.bytesToUnsignedInt(minor)
        );
    }
}
