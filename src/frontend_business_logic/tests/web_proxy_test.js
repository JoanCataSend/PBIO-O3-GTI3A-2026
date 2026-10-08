/*
 * Archivo: web_proxy_test.js
 * Descripción: prueba estática del proxy de lógica de negocio del cliente web.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verifica paridad de interfaz y encapsulación de fetch.
 */

"use strict";

const fs = require("fs");
const path = require("path");
const assert = require("assert");

const proxyPath = path.join(__dirname, "..", "web", "LogicaFake.js");
const source = fs.readFileSync(proxyPath, "utf8");

assert.match(source, /const API\s*=\s*"api\.php"/);
assert.doesNotMatch(source, /https?:\/\//);
assert.match(source, /\bfetch\s*\(/);

for (const nombre of [
    "probarConexion",
    "listarMedidas",
    "listarDispositivos",
    "listarTiposMedida"
]) {
    assert.match(source, new RegExp(`async function ${nombre}\\s*\\(`));
}

assert.doesNotMatch(source, /\bdocument\.|getElementById|innerHTML/);
assert.match(source, /\?accion=health/);
assert.match(source, /\?accion=dispositivos/);
assert.match(source, /\?accion=tipos/);

console.log("FRONTEND BUSINESS LOGIC WEB TEST: OK");
