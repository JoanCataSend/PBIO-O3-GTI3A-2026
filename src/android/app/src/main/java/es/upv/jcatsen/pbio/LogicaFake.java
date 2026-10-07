/*
 * Archivo: LogicaFake.java
 * Descripción: adaptador lógico del cliente Android para invocar el backend REST
 *              sin mezclar HTTP con MainActivity.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: lógica fake del cliente Android para el Sprint 0.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONException;
import org.json.JSONObject;

public class LogicaFake {

    // Servidor real del proyecto en Plesk.
    public static final String URL_API =
            "https://jcatsen.upv.edu.es/biometria/api.php";

    public static final String URL_HEALTH =
            URL_API + "?accion=health";

    /**
     * --------------------
     * Diseño lógico: comprobarServidor() --> estado: Text
     * Descripción: comprueba que Android puede alcanzar la API y recibir JSON válido.
     * --------------------
     */
    public static void comprobarServidor(
            PeticionarioREST.Callback callback
    ) {
        PeticionarioREST.getJson(
                URL_HEALTH,
                callback
        );
    }

    /**
     * --------------------
     * Diseño lógico: datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
     * Descripción: serializa la medida y delega el POST en PeticionarioREST.
     * --------------------
     */
    public static void insertarMedida(
            MedidaEntrada datos,
            PeticionarioREST.Callback callback
    ) {
        try {
            JSONObject json = datos.toJson();

            PeticionarioREST.postJson(
                    URL_API,
                    json,
                    callback
            );

        } catch (JSONException e) {
            callback.error(
                    "JSON: " + e.getMessage()
            );
        }
    }
}
