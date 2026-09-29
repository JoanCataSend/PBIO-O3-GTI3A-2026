package org.jordi.prueba2025;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProtocolUnitTest {

    @Test
    public void majorContainsMeasurementIdAndCounter() {
        int measurementId = 14;
        int counter = 16;

        int major = (measurementId << 8) | counter;

        assertEquals(3600, major);
        assertEquals(14, (major >> 8) & 0xFF);
        assertEquals(16, major & 0xFF);
    }

    @Test
    public void signedMinorDecodesNegativeTemperature() {
        byte[] minor = new byte[] {
                (byte) 0xFF,
                (byte) 0xE2
        };

        assertEquals(
                -30,
                Utilidades.bytesToSignedInt16(minor)
        );
    }
}
