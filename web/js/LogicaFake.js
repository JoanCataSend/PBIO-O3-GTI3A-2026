"use strict";

const LogicaFake = (() => {

    const API = "api.php";

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

    // listarMedidas() --> [MedidaVista]
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

    // listarDispositivos() --> [Dispositivo]
    async function listarDispositivos() {
        return pedir(
            `${API}?accion=dispositivos`
        );
    }

    // listarTiposMedida() --> [TipoMedida]
    async function listarTiposMedida() {
        return pedir(
            `${API}?accion=tipos`
        );
    }

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
