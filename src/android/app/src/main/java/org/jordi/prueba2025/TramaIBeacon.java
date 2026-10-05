/*
 * Archivo: TramaIBeacon.java
 * Descripción: analiza bytes de advertising BLE y extrae UUID, Major, Minor y TxPower.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: decodificación de trama iBeacon para el protocolo PBIO.
 */

package org.jordi.prueba2025;

import java.util.Arrays;

public class TramaIBeacon {

    private final byte[] losBytes;

    private byte[] uuid = new byte[0];
    private byte[] major = new byte[0];
    private byte[] minor = new byte[0];

    private byte txPower = 0;

    private boolean valida = false;

    /**
     * --------------------
     * Diseño lógico: bytes:[N] --> TramaIBeacon() -->
     * Descripción: construye la trama y analiza inmediatamente su contenido.
     * --------------------
     */
    public TramaIBeacon(byte[] bytes) {

        this.losBytes =
                bytes == null
                        ? new byte[0]
                        : bytes;

        analizar();
    }

    /**
     * --------------------
     * Diseño lógico: analizar() -->
     * Descripción: localiza el prefijo Apple+iBeacon y extrae los campos de la trama.
     * --------------------
     */
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

    /**
     * --------------------
     * Diseño lógico: esValida() --> valida:VoF. Consulta si se encontró una trama iBeacon.
     * --------------------
     */
    public boolean esValida() {
        return valida;
    }

    /**
     * --------------------
     * Diseño lógico: getUUID() --> uuid:[N]_16. Devuelve el UUID extraído.
     * --------------------
     */
    public byte[] getUUID() {
        return uuid;
    }

    /**
     * --------------------
     * Diseño lógico: getMajor() --> major:[N]_2. Devuelve los dos bytes de Major.
     * --------------------
     */
    public byte[] getMajor() {
        return major;
    }

    /**
     * --------------------
     * Diseño lógico: getMinor() --> minor:[N]_2. Devuelve los dos bytes de Minor.
     * --------------------
     */
    public byte[] getMinor() {
        return minor;
    }

    /**
     * --------------------
     * Diseño lógico: getTxPower() --> txPower:Z. Devuelve TxPower de la trama.
     * --------------------
     */
    public byte getTxPower() {
        return txPower;
    }

    /**
     * --------------------
     * Diseño lógico: getLosBytes() --> bytes:[N]. Devuelve los bytes originales.
     * --------------------
     */
    public byte[] getLosBytes() {
        return losBytes;
    }
}
