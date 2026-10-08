/*
 * Archivo: EstadoBD.java
 * Descripción: resultado de dominio de la operación probarConexion del backend.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: tipo de dominio compartido por la interfaz fake Android.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONObject;

public final class EstadoBD {

    public final boolean ok;
    public final String database;

    /**
     * --------------------
     * Diseño lógico: ok: B, database: Text --> EstadoBD()
     * Descripción: construye un estado de persistencia inmutable.
     * --------------------
     */
    public EstadoBD(boolean ok, String database) {
        this.ok = ok;
        this.database = database;
    }

    /**
     * --------------------
     * Diseño lógico: texto: Text --> fromJson() --> estado: EstadoBD
     * Descripción: transforma la representación recibida en el tipo de dominio EstadoBD.
     * --------------------
     */
    public static EstadoBD fromJson(JSONObject json) {
        return new EstadoBD(
                json.optBoolean("ok", false),
                json.optString("database", "")
        );
    }
}
