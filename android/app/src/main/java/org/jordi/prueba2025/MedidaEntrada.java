package org.jordi.prueba2025;

import org.json.JSONException;
import org.json.JSONObject;

public class MedidaEntrada {

    private final String uuid;
    private final int tipoMedidaId;
    private final int valor;
    private final int contador;
    private final int rssi;

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
