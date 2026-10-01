/*
 * Archivo: ProtocolUnitTest.java
 * Descripción: tests unitarios del protocolo Major/Minor usado entre firmware y Android.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: verificación automática de codificación/decodificación del protocolo PBIO.
 */

package org.jordi.prueba2025;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProtocolUnitTest {

    /**
     * Diseño lógico: id:N, contador:N --> codificarMajor() --> major:N
     * Criterio: ID 14 y contador 16 deben producir Major 3600 y ser reversibles.
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
     * Diseño lógico: minor:[N]_2 --> decodificarTemperatura() --> temperatura:Z
     * Criterio Sprint 0: FF F4 debe decodificarse como -12 °C.
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
     * Diseño lógico: minor:[N]_2 --> decodificarO3() --> valor:N
     * Criterio Sprint 0: 00 7B debe decodificarse como 123 ppb.
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
