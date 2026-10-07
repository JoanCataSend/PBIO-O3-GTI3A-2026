/*
 * Archivo: Utilidades.java
 * Descripción: conversiones auxiliares de arrays de bytes usadas por el protocolo BLE.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: conversiones de texto/hex/enteros con y sin signo.
 */

package es.upv.jcatsen.pbio;

public class Utilidades {

    /**
     * --------------------
     * Diseño lógico: bytes: [N] --> bytesToString() --> texto: Text
     * Descripción: interpreta cada byte como un carácter de 8 bits.
     * --------------------
     */
    public static String bytesToString(byte[] bytes) {

        if (bytes == null) {
            return "";
        }

        StringBuilder sb =
                new StringBuilder();

        for (byte b : bytes) {

            sb.append(
                    (char)(b & 0xFF)
            );
        }

        return sb.toString();
    }

    /**
     * --------------------
     * Diseño lógico: bytes: [N] --> bytesToHexString() --> hexadecimal: Text
     * Descripción: representa los bytes en hexadecimal separados por dos puntos.
     * --------------------
     */
    public static String bytesToHexString(byte[] bytes) {

        if (bytes == null) {
            return "";
        }

        StringBuilder sb =
                new StringBuilder();

        for (byte b : bytes) {

            sb.append(
                    String.format(
                            "%02x:",
                            b & 0xFF
                    )
            );
        }

        return sb.toString();
    }

    /**
     * --------------------
     * Diseño lógico: bytes: [N] --> bytesToUnsignedInt() --> valor: N
     * Descripción: interpreta los bytes big-endian como entero sin signo.
     * --------------------
     */
    public static int bytesToUnsignedInt(byte[] bytes) {

        if (bytes == null) {
            return 0;
        }

        int res = 0;

        for (byte b : bytes) {

            res =
                    (res << 8)
                            |
                    (b & 0xFF);
        }

        return res;
    }

    /**
     * --------------------
     * Diseño lógico: bytes: [N]_2 --> bytesToSignedInt16() --> valor: Z
     * Descripción: interpreta dos bytes big-endian en complemento a dos.
     * --------------------
     */
    public static int bytesToSignedInt16(byte[] bytes) {

        int u =
                bytesToUnsignedInt(bytes);

        if (bytes != null &&
            bytes.length == 2 &&
            (u & 0x8000) != 0) {

            return u - 0x10000;
        }

        return u;
    }
}
