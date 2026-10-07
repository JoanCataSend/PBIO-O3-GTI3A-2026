/*
 * Archivo: LogicaFake.js
 * Descripción: adaptador lógico del navegador para consultar la API REST.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: interfaz asíncrona para medidas, dispositivos, tipos y health.
 */

"use strict";

const LogicaFake = (() => {

    const API = "api.php";

    /**
     * --------------------
     * Diseño lógico: url: Text, opciones: PeticionHTTP --> pedir() --> datos: Text
     * Descripción: ejecuta fetch, interpreta Text y normaliza errores HTTP.
     * --------------------
     */
    async function pedir(url, opciones = {}) {

        const respuesta =
            await fetch(url, opciones);

        let datos = null;

        try {
            datos =
                await respuesta.json();
        } catch (_) {
            datos = null;
        }

        if (!respuesta.ok) {

            throw new Error(
                datos?.error
                || `HTTP ${respuesta.status}`
            );
        }

        return datos;
    }

    /**
     * --------------------
     * Diseño lógico: filtros: FiltrosMedida --> listarMedidas() --> medidas: [MedidaVista]
     * Descripción: convierte filtros no vacíos en query string y consulta medidas.
     * --------------------
     */
    async function listarMedidas(
        filtros = {}
    ) {
        const params =
            new URLSearchParams();

        for (
            const [clave, valor]
            of Object.entries(filtros)
        ) {
            if (
                valor !== ""
                && valor !== null
                && valor !== undefined
            ) {
                params.set(
                    clave,
                    valor
                );
            }
        }

        const query =
            params.toString();

        return pedir(
            API
            + (query ? `?${query}` : "")
        );
    }

    /**
     * --------------------
     * Diseño lógico: listarDispositivos() --> dispositivos: [Dispositivo]
     * Descripción: obtiene el catálogo de dispositivos registrado por el backend.
     * --------------------
     */
    async function listarDispositivos() {
        return pedir(
            `${API}?accion=dispositivos`
        );
    }

    /**
     * --------------------
     * Diseño lógico: listarTiposMedida() --> tipos: [TipoMedida]
     * Descripción: obtiene el catálogo de tipos de medida registrado por el backend.
     * --------------------
     */
    async function listarTiposMedida() {
        return pedir(
            `${API}?accion=tipos`
        );
    }

    /**
     * --------------------
     * Diseño lógico: health() --> estado: EstadoBD
     * Descripción: comprueba que la API y la base de datos responden correctamente.
     * --------------------
     */
    async function health() {
        return pedir(
            `${API}?accion=health`
        );
    }

    return {
        listarMedidas,
        listarDispositivos,
        listarTiposMedida,
        health
    };
})();
