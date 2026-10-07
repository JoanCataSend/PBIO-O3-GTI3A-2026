/*
 * Archivo: MedidaEntrada.java
 * Descripción: tipo de datos enviado por Android al servidor para registrar una medida.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: representación y serialización de MedidaEntrada.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONException;
import org.json.JSONObject;

public class MedidaEntrada {

    private final String uuid;
    private final int tipoMedidaId;
    private final int valor;
    private final int contador;
    private final int rssi;

    /**
     * --------------------
     * Diseño lógico:
     * uuid: Text, tipo_medida_id: N, valor: Z, contador: N, rssi: Z --> MedidaEntrada() -->
     * Descripción: construye una medida de entrada inmutable.
     * --------------------
     */
    public MedidaEntrada(
            String uuid,
            int tipoMedidaId,
            int valor,
            int contador,
            int rssi
    ) {
        this.uuid = uuid;
        this.tipoMedidaId = tipoMedidaId;
        this.valor = valor;
        this.contador = contador;
        this.rssi = rssi;
    }

    /**
     * --------------------
     * Diseño lógico: toJson() --> json: Json
     * Descripción: serializa todos los campos con los nombres esperados por la API.
     * --------------------
     */
    public JSONObject toJson() throws JSONException {

        JSONObject json = new JSONObject();

        json.put("uuid", uuid);
        json.put("tipoMedidaId", tipoMedidaId);
        json.put("valor", valor);
        json.put("contador", contador);
        json.put("rssi", rssi);

        return json;
    }
}
