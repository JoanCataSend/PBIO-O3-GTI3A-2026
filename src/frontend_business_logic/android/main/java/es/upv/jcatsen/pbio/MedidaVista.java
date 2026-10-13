/*
 * Archivo: MedidaVista.java
 * Descripción: vista de dominio de una medida devuelta por la lógica de negocio remota.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-13
 * Autor: Joan Catala Sendra
 * Aportación: adapta el tipo de retorno Android al esquema simplificado de Sprint 0.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONObject;

public final class MedidaVista {

    public final String dispositivoId;
    public final String dispositivo;
    public final int tipoMedidaId;
    public final String tipoMedida;
    public final String unidad;
    public final int valor;
    public final String fechaHora;

    /**
     * --------------------
     * Diseño lógico:
     * dispositivo_id: Text, dispositivo: Text, tipo_medida_id: N,
     * tipo_medida: Text, unidad: Text, valor: Z, fecha_hora: Text
     * --> MedidaVista()
     * Descripción: construye una vista de medida inmutable.
     * --------------------
     */
    public MedidaVista(
            String dispositivoId,
            String dispositivo,
            int tipoMedidaId,
            String tipoMedida,
            String unidad,
            int valor,
            String fechaHora
    ) {
        this.dispositivoId = dispositivoId;
        this.dispositivo = dispositivo;
        this.tipoMedidaId = tipoMedidaId;
        this.tipoMedida = tipoMedida;
        this.unidad = unidad;
        this.valor = valor;
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
                json.optString("dispositivoId", ""),
                json.optString("dispositivo", ""),
                json.optInt("tipoMedidaId"),
                json.optString("tipoMedida", ""),
                json.optString("unidad", ""),
                json.optInt("valor"),
                json.optString("fechaHora", "")
        );
    }
}
