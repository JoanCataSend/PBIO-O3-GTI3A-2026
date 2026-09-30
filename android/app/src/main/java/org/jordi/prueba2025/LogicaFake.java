package org.jordi.prueba2025;

import org.json.JSONException;
import org.json.JSONObject;

public class LogicaFake {

    // Servidor real del proyecto en Plesk.
    public static final String URL_API =
            "https://jcatsen.upv.edu.es/biometria/api.php";

    // datos: MedidaEntrada --> insertarMedida() --> Medida
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
