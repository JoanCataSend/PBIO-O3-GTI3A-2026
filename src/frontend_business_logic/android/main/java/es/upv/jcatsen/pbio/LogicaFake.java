/*
 * Archivo: LogicaFake.java
 * Descripción: proxy de lógica de negocio del cliente Android. Expone a la GUI
 *              un subconjunto de la misma interfaz lógica que el servidor.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: proxy Android con comunicación encapsulada y tipos de dominio.
 */

package es.upv.jcatsen.pbio;

import org.json.JSONException;

public final class LogicaFake {

    static final String URL_API =
            "https://jcatsen.upv.edu.es/biometria/api.php";

    static final String URL_HEALTH =
            URL_API + "?accion=health";

    public interface CallbackEstadoBD {
        /**
         * --------------------
         * Diseño lógico: estado: EstadoBD --> correcto()
         * Descripción: entrega a la GUI el resultado de probarConexion.
         * --------------------
         */
        void correcto(EstadoBD estado);

        /**
         * --------------------
         * Diseño lógico: mensaje: Text --> error()
         * Descripción: entrega a la GUI un mensaje estable cuando la operación falla.
         * --------------------
         */
        void error(String mensaje);
    }

    public interface CallbackMedidaVista {
        /**
         * --------------------
         * Diseño lógico: medida: MedidaVista --> correcto()
         * Descripción: entrega a la GUI la medida creada por insertarMedida.
         * --------------------
         */
        void correcto(MedidaVista medida);

        /**
         * --------------------
         * Diseño lógico: mensaje: Text --> error()
         * Descripción: entrega a la GUI un mensaje estable cuando la operación falla.
         * --------------------
         */
        void error(String mensaje);
    }

    private LogicaFake() {
    }

    /**
     * --------------------
     * Diseño lógico: probarConexion() --> estado: EstadoBD
     * Descripción: expone la misma operación lógica que el backend y delega el transporte.
     * --------------------
     */
    public static void probarConexion(
            CallbackEstadoBD callback
    ) {
        PeticionarioREST.getJson(
                URL_HEALTH,
                new PeticionarioREST.Callback() {
                    /**
                     * --------------------
                     * Diseño lógico: respuesta: Text --> correcto()
                     * Descripción: adapta la respuesta recibida al tipo de dominio EstadoBD.
                     * --------------------
                     */
                    @Override
                    public void correcto(org.json.JSONObject respuesta) {
                        callback.correcto(EstadoBD.fromJson(respuesta));
                    }

                    /**
                     * --------------------
                     * Diseño lógico: mensaje: Text --> error()
                     * Descripción: propaga al contrato del proxy el error de transporte normalizado.
                     * --------------------
                     */
                    @Override
                    public void error(String mensaje) {
                        callback.error(mensaje);
                    }
                }
        );
    }

    /**
     * --------------------
     * Diseño lógico: datos: MedidaEntrada --> insertarMedida() --> medida: MedidaVista
     * Descripción: expone la misma operación lógica que el backend y delega el transporte.
     * --------------------
     */
    public static void insertarMedida(
            MedidaEntrada datos,
            CallbackMedidaVista callback
    ) {
        try {
            PeticionarioREST.postJson(
                    URL_API,
                    datos.toJson(),
                    new PeticionarioREST.Callback() {
                        /**
                         * --------------------
                         * Diseño lógico: respuesta: Text --> correcto()
                         * Descripción: adapta la respuesta recibida al tipo de dominio MedidaVista.
                         * --------------------
                         */
                        @Override
                        public void correcto(org.json.JSONObject respuesta) {
                            callback.correcto(MedidaVista.fromJson(respuesta));
                        }

                        /**
                         * --------------------
                         * Diseño lógico: mensaje: Text --> error()
                         * Descripción: propaga al contrato del proxy el error de transporte normalizado.
                         * --------------------
                         */
                        @Override
                        public void error(String mensaje) {
                            callback.error(mensaje);
                        }
                    }
            );

        } catch (JSONException e) {
            callback.error(
                    "JSON: " + e.getMessage()
            );
        }
    }
}
