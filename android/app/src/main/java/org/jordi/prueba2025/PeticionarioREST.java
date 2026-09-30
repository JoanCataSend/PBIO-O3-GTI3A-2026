package org.jordi.prueba2025;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeticionarioREST {

    public interface Callback {
        void correcto(JSONObject respuesta);
        void error(String mensaje);
    }

    private static final ExecutorService EJECUTOR =
            Executors.newSingleThreadExecutor();

    public static void postJson(
            String urlTexto,
            JSONObject datos,
            Callback callback
    ) {

        EJECUTOR.execute(() -> {

            HttpURLConnection conexion = null;

            try {

                URL url = new URL(urlTexto);

                conexion =
                        (HttpURLConnection) url.openConnection();

                conexion.setRequestMethod("POST");
                conexion.setConnectTimeout(6000);
                conexion.setReadTimeout(6000);
                conexion.setDoOutput(true);

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                byte[] cuerpo =
                        datos.toString()
                                .getBytes(StandardCharsets.UTF_8);

                try (OutputStream os =
                             conexion.getOutputStream()) {
                    os.write(cuerpo);
                }

                int codigo =
                        conexion.getResponseCode();

                InputStream entrada =
                        codigo >= 200 && codigo < 300
                                ? conexion.getInputStream()
                                : conexion.getErrorStream();

                String texto =
                        leerTexto(entrada);

                if (codigo >= 200 && codigo < 300) {

                    callback.correcto(
                            texto.isEmpty()
                                    ? new JSONObject()
                                    : new JSONObject(texto)
                    );

                } else {

                    callback.error(
                            "HTTP " + codigo + ": " + texto
                    );
                }

            } catch (Exception e) {

                callback.error(
                        e.getClass().getSimpleName()
                                + ": "
                                + e.getMessage()
                );

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }

    private static String leerTexto(
            InputStream entrada
    ) throws Exception {

        if (entrada == null) {
            return "";
        }

        StringBuilder sb =
                new StringBuilder();

        try (BufferedReader br =
                     new BufferedReader(
                             new InputStreamReader(
                                     entrada,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String linea;

            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
        }

        return sb.toString();
    }
}
