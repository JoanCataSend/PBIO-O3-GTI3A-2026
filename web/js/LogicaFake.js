/*
 * Archivo: LogicaFake.js
 * Descripción: adaptador lógico del navegador para consultar la API REST.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: interfaz asíncrona para medidas, dispositivos, tipos y health.
 */

"use strict";

const LogicaFake = (() => {

    const API = "api.php";

    /**
     * Diseño lógico: url:Texto, opciones:PeticionHTTP --> pedir() --> JSON | Error
     * Descripción: ejecuta fetch, interpreta JSON y normaliza errores HTTP.
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
     * Diseño lógico: filtros:FiltrosMedida --> listarMedidas() --> [MedidaVista] | Error
     * Descripción: convierte filtros no vacíos en query string y consulta medidas.
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

    /** Diseño lógico: listarDispositivos() --> [Dispositivo] | Error. */
    async function listarDispositivos() {
        return pedir(
            `${API}?accion=dispositivos`
        );
    }

    /** Diseño lógico: listarTiposMedida() --> [TipoMedida] | Error. */
    async function listarTiposMedida() {
        return pedir(
            `${API}?accion=tipos`
        );
    }

    /** Diseño lógico: health() --> EstadoBD | Error. */
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
