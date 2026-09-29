package org.jordi.prueba2025;

import java.util.Arrays;

public class TramaIBeacon {

    private final byte[] losBytes;

    private byte[] uuid = new byte[0];
    private byte[] major = new byte[0];
    private byte[] minor = new byte[0];

    private byte txPower = 0;

    private boolean valida = false;

    public TramaIBeacon(byte[] bytes) {

        this.losBytes =
                bytes == null
                        ? new byte[0]
                        : bytes;

        analizar();
    }

    private void analizar() {

        // Buscar:
        // 4C 00 = Apple Company ID
        // 02 15 = iBeacon
        for (int i = 0; i + 24 < losBytes.length; i++) {

            if ((losBytes[i] & 0xFF) == 0x4C &&
                (losBytes[i + 1] & 0xFF) == 0x00 &&
                (losBytes[i + 2] & 0xFF) == 0x02 &&
                (losBytes[i + 3] & 0xFF) == 0x15) {

                uuid =
                        Arrays.copyOfRange(
                                losBytes,
                                i + 4,
                                i + 20
                        );

                major =
                        Arrays.copyOfRange(
                                losBytes,
                                i + 20,
                                i + 22
                        );

                minor =
                        Arrays.copyOfRange(
                                losBytes,
                                i + 22,
                                i + 24
                        );

                txPower =
                        losBytes[i + 24];

                valida = true;

                return;
            }
        }
    }

    public boolean esValida() {
        return valida;
    }

    public byte[] getUUID() {
        return uuid;
    }

    public byte[] getMajor() {
        return major;
    }

    public byte[] getMinor() {
        return minor;
    }

    public byte getTxPower() {
        return txPower;
    }

    public byte[] getLosBytes() {
        return losBytes;
    }
}
