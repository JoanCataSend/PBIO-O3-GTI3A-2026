/*
 * Archivo: web_unit_test.js
 * Descripción: pruebas unitarias de funciones puras y contrato HTTP de la web.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verifica fechas, escape HTML y uso de API relativa.
 */

"use strict";

const fs = require("fs");
const path = require("path");
const vm = require("vm");
const assert = require("assert");

const appPath = path.join(__dirname, "..", "js", "app.js");
const fakePath = path.join(__dirname, "..", "js", "LogicaFake.js");
let source = fs.readFileSync(appPath, "utf8");
const fakeSource = fs.readFileSync(fakePath, "utf8");

source = source.replace(/\niniciar\(\);\s*$/, "\n");
source += "\nglobalThis.__pbioTestExports = { fechaSql, escapar };\n";

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

const context = {
    console,
    setInterval() {},
    clearInterval() {},
    document: {
        getElementById() { return fakeElement(); },
        createElement() { return fakeElement(); }
    },
    window: { devicePixelRatio: 1 },
    LogicaFake: {},
    Date,
    Math,
    Number,
    String
};
context.globalThis = context;
vm.createContext(context);
vm.runInContext(source, context, { filename: appPath });

const { fechaSql, escapar } = context.__pbioTestExports;
assert.strictEqual(fechaSql(""), "");
assert.strictEqual(fechaSql("2026-10-07T10:30"), "2026-10-07 10:30:00");
assert.strictEqual(fechaSql("2026-10-07T10:30:45"), "2026-10-07 10:30:45");
assert.strictEqual(
    escapar(`<tag a="1">Tom & 'Ana'</tag>`),
    "&lt;tag a=&quot;1&quot;&gt;Tom &amp; &#039;Ana&#039;&lt;/tag&gt;"
);
assert.match(fakeSource, /const API\s*=\s*"api\.php"/);
assert.doesNotMatch(fakeSource, /https?:\/\//);

console.log("WEB UNIT TEST: OK");
