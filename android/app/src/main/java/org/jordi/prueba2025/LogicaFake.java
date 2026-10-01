/*
 * Archivo: LogicaFake.java
 * Descripción: adaptador lógico del cliente Android para invocar la operación
 *              insertarMedida mediante REST sin mezclar HTTP con MainActivity.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: lógica fake del cliente Android para el Sprint 0.
 */

package org.jordi.prueba2025;

import org.json.JSONException;
import org.json.JSONObject;

public class LogicaFake {

    // Servidor real del proyecto en Plesk.
    public static final String URL_API =
            "https://jcatsen.upv.edu.es/biometria/api.php";

    /**
     * Diseño lógico: datos:MedidaEntrada --> insertarMedida() --> MedidaVista | Error
     * Descripción: serializa la medida y delega el POST en PeticionarioREST.
     */
    public static void insertarMedida(
            MedidaEntrada datos,
            PeticionarioREST.Callback callback
    ) {

        try {

            JSONObject json =
                    datos.toJson();

            PeticionarioREST.postJson(
                    URL_API,
                    json,
                    callback
            );

        } catch (JSONException e) {

            callback.error(
                    "No se pudo crear el JSON: "
                            + e.getMessage()
            );
        }
    }
}
