/*
 * Archivo: gui_unit_test.js
 * Descripción: pruebas unitarias de la GUI web sin navegador real ni transporte.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verifica transformaciones, resumen, errores y desacoplamiento de red.
 */

"use strict";

const fs = require("fs");
const path = require("path");
const vm = require("vm");
const assert = require("assert");

const appPath = path.join(__dirname, "..", "js", "app.js");
let source = fs.readFileSync(appPath, "utf8");

assert.doesNotMatch(source, /\bfetch\s*\(/);
assert.doesNotMatch(source, /XMLHttpRequest|WebSocket|https?:\/\//);
assert.match(source, /LogicaFake\.probarConexion\s*\(/);
assert.match(source, /LogicaFake\.listarMedidas\s*\(/);
assert.match(source, /LogicaFake\.listarDispositivos\s*\(/);
assert.match(source, /LogicaFake\.listarTiposMedida\s*\(/);

source = source.replace(/\niniciar\(\);\s*$/, "\n");
source += "\nglobalThis.__pbioTestExports = { fechaSql, escapar, pintarResumen, mostrarError };\n";

/**
 * --------------------
 * Diseño lógico: fakeElement() --> elemento: Text
 * Descripción: crea un doble mínimo de elemento DOM para ejecutar pruebas sin navegador.
 * --------------------
 */
const fakeElement = () => ({
    addEventListener() {},
    appendChild() {},
    getContext() { return {}; },
    getBoundingClientRect() { return { width: 800, height: 340 }; },
    value: "",
    textContent: "",
    className: "",
    innerHTML: "",
    hidden: false
});

const elementos = {};

const context = {
    console,
    setInterval() {},
    clearInterval() {},
    document: {
        getElementById(id) {
            if (!elementos[id]) { elementos[id] = fakeElement(); }
            return elementos[id];
        },
        createElement() { return fakeElement(); }
    },
    window: { devicePixelRatio: 1 },
    LogicaFake: {
        async probarConexion() { return { ok: true, database: "test" }; },
        async listarMedidas() { return []; },
        async listarDispositivos() { return []; },
        async listarTiposMedida() { return []; }
    },
    Date,
    Math,
    Number,
    String
};
context.globalThis = context;
vm.createContext(context);
vm.runInContext(source, context, { filename: appPath });

const { fechaSql, escapar, pintarResumen, mostrarError } = context.__pbioTestExports;
assert.strictEqual(fechaSql(""), "");
assert.strictEqual(fechaSql("2026-10-07T10:30"), "2026-10-07 10:30:00");
assert.strictEqual(fechaSql("2026-10-07T10:30:45"), "2026-10-07 10:30:45");
assert.strictEqual(
    escapar(`<tag a="1">Tom & 'Ana'</tag>`),
    "&lt;tag a=&quot;1&quot;&gt;Tom &amp; &#039;Ana&#039;&lt;/tag&gt;"
);

pintarResumen([
    { tipoMedidaId: 14, valor: 321, rssi: -50 },
    { tipoMedidaId: 12, valor: -12, rssi: -60 }
]);
assert.strictEqual(elementos.valorO3.textContent, 321);
assert.strictEqual(elementos.valorTemperatura.textContent, -12);
assert.strictEqual(elementos.valorRssi.textContent, -50);

mostrarError("fallo de prueba");
assert.strictEqual(elementos.estadoConexion.textContent, "Error de comunicación");
assert.strictEqual(elementos.estadoConexion.className, "estado error");

console.log("GUI UNIT TEST: OK");
