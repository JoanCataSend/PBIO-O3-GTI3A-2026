/*
 * Archivo: PeticionarioREST.java
 * Descripción: cliente HTTP/HTTPS mínimo para intercambiar JSON fuera del hilo de UI.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: transporte REST robusto con diagnóstico explícito de red, HTTP y TLS.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;

final class PeticionarioREST {

    /**
     * Resultado lógico asíncrono del transporte REST.
     */
    public interface Callback {
        /**
         * --------------------
         * Diseño lógico: respuesta: Text --> correcto()
         * Descripción: comunica al cliente que la petición terminó correctamente.
         * --------------------
         */
        void correcto(JSONObject respuesta);

        /**
         * --------------------
         * Diseño lógico: mensaje: Text --> error()
         * Descripción: comunica al cliente el motivo de un fallo de petición.
         * --------------------
         */
        void error(String mensaje);
    }

    private static final ExecutorService EJECUTOR =
            Executors.newSingleThreadExecutor();

    private static final int TIMEOUT_MS = 10000;

    /**
     * --------------------
     * Diseño lógico: url: Text --> getJson() --> respuesta: Text
     * Descripción: realiza un GET esperando una respuesta JSON.
     * --------------------
     */
    public static void getJson(
            String urlTexto,
            Callback callback
    ) {
        ejecutarJson("GET", urlTexto, null, callback);
    }

    /**
     * --------------------
     * Diseño lógico: url: Text, datos: Text --> postJson() --> respuesta: Text
     * Descripción: realiza un POST JSON con longitud conocida y espera una respuesta JSON.
     * --------------------
     */
    public static void postJson(
            String urlTexto,
            JSONObject datos,
            Callback callback
    ) {
        ejecutarJson("POST", urlTexto, datos, callback);
    }

    /**
     * --------------------
     * Diseño lógico: metodo: Text, url: Text, datos: Text --> ejecutarJson() --> respuesta: Text
     * Descripción: ejecuta una petición JSON y normaliza errores HTTP, red, DNS y TLS.
     * --------------------
     */
    private static void ejecutarJson(
            String metodo,
            String urlTexto,
            JSONObject datos,
            Callback callback
    ) {
        EJECUTOR.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                URL url = new URL(urlTexto);
                conexion = (HttpURLConnection) url.openConnection();

                conexion.setRequestMethod(metodo);
                conexion.setConnectTimeout(TIMEOUT_MS);
                conexion.setReadTimeout(TIMEOUT_MS);
                conexion.setUseCaches(false);
                conexion.setInstanceFollowRedirects(true);
                conexion.setRequestProperty("Accept", "application/json");
                conexion.setRequestProperty("User-Agent", "PBIO-Android/1.0");
                conexion.setRequestProperty("Connection", "close");

                if ("POST".equals(metodo)) {
                    byte[] cuerpo = datos == null
                            ? new byte[0]
                            : datos.toString().getBytes(StandardCharsets.UTF_8);

                    conexion.setDoOutput(true);
                    conexion.setRequestProperty(
                            "Content-Type",
                            "application/json; charset=UTF-8"
                    );
                    conexion.setFixedLengthStreamingMode(cuerpo.length);

                    try (OutputStream os = conexion.getOutputStream()) {
                        os.write(cuerpo);
                        os.flush();
                    }
                }

                int codigo = conexion.getResponseCode();
                InputStream entrada = codigo >= 200 && codigo < 300
                        ? conexion.getInputStream()
                        : conexion.getErrorStream();

                String texto = leerTexto(entrada);

                if (codigo >= 200 && codigo < 300) {
                    callback.correcto(
                            texto.isEmpty()
                                    ? new JSONObject()
                                    : new JSONObject(texto)
                    );
                } else {
                    callback.error(
                            "HTTP " + codigo
                                    + (texto.isEmpty() ? "" : ": " + texto)
                    );
                }

            } catch (SSLHandshakeException e) {
                callback.error(
                        "SSL/TLS: no se pudo validar el certificado del servidor"
                );

            } catch (UnknownHostException e) {
                callback.error(
                        "Red/DNS: no se puede localizar jcatsen.upv.edu.es"
                );

            } catch (SocketTimeoutException e) {
                callback.error(
                        "Red: tiempo de espera agotado"
                );

            } catch (IOException e) {
                callback.error(
                        "Red: " + mensajeSeguro(e)
                );

            } catch (Exception e) {
                callback.error(
                        e.getClass().getSimpleName()
                                + ": "
                                + mensajeSeguro(e)
                );

            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }

    /**
     * --------------------
     * Diseño lógico: entrada: Text --> leerTexto() --> texto: Text
     * Descripción: consume un flujo UTF-8 y devuelve su contenido completo.
     * --------------------
     */
    private static String leerTexto(
            InputStream entrada
    ) throws IOException {
        if (entrada == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

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

    /**
     * --------------------
     * Diseño lógico: error: Text --> mensajeSeguro() --> mensaje: Text
     * Descripción: obtiene un texto de error estable sin devolver null a la interfaz.
     * --------------------
     */
    private static String mensajeSeguro(Exception e) {
        String mensaje = e.getMessage();
        return mensaje == null || mensaje.trim().isEmpty()
                ? e.getClass().getSimpleName()
                : mensaje;
    }
}
