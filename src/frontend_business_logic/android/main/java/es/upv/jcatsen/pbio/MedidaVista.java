/*
 * Archivo: MedidaVista.java
 * Descripción: vista de dominio de una medida devuelta por la lógica de negocio remota.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: tipo de retorno Android idéntico al contrato lógico del backend.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONObject;

public final class MedidaVista {

    public final long medidaId;
    public final int dispositivoId;
    public final String uuid;
    public final String dispositivo;
    public final int tipoMedidaId;
    public final String tipoMedida;
    public final String unidad;
    public final int valor;
    public final int contador;
    public final int rssi;
    public final String fechaHora;

    /**
     * --------------------
     * Diseño lógico:
     * medida_id: N, dispositivo_id: N, uuid: Text, dispositivo: Text,
     * tipo_medida_id: N, tipo_medida: Text, unidad: Text, valor: Z,
     * contador: N, rssi: Z, fecha_hora: Text --> MedidaVista()
     * Descripción: construye una vista de medida inmutable.
     * --------------------
     */
    public MedidaVista(
            long medidaId,
            int dispositivoId,
            String uuid,
            String dispositivo,
            int tipoMedidaId,
            String tipoMedida,
            String unidad,
            int valor,
            int contador,
            int rssi,
            String fechaHora
    ) {
        this.medidaId = medidaId;
        this.dispositivoId = dispositivoId;
        this.uuid = uuid;
        this.dispositivo = dispositivo;
        this.tipoMedidaId = tipoMedidaId;
        this.tipoMedida = tipoMedida;
        this.unidad = unidad;
        this.valor = valor;
        this.contador = contador;
        this.rssi = rssi;
        this.fechaHora = fechaHora;
    }

    /**
     * --------------------
     * Diseño lógico: texto: Text --> fromJson() --> medida: MedidaVista
     * Descripción: transforma la representación recibida en MedidaVista.
     * --------------------
     */
    public static MedidaVista fromJson(JSONObject json) {
        return new MedidaVista(
                json.optLong("medidaId"),
                json.optInt("dispositivoId"),
                json.optString("uuid", ""),
                json.optString("dispositivo", ""),
                json.optInt("tipoMedidaId"),
                json.optString("tipoMedida", ""),
                json.optString("unidad", ""),
                json.optInt("valor"),
                json.optInt("contador"),
                json.optInt("rssi"),
                json.optString("fechaHora", "")
        );
    }
}
