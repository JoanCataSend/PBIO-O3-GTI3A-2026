package org.jordi.prueba2025;

public class Utilidades {

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
