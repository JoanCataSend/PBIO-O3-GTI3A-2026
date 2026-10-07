/*
 * Archivo: app.js
 * Descripción: controlador de la interfaz web del Sprint 0. Carga catálogos,
 *              consulta medidas y actualiza resumen, tabla y gráfica.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: UX del navegador y refresco automático cada 5 segundos.
 */

"use strict";

const ui = {
    estado:
        document.getElementById(
            "estadoConexion"
        ),
    dispositivo:
        document.getElementById(
            "filtroDispositivo"
        ),
    tipo:
        document.getElementById(
            "filtroTipo"
        ),
    desde:
        document.getElementById(
            "filtroDesde"
        ),
    hasta:
        document.getElementById(
            "filtroHasta"
        ),
    boton:
        document.getElementById(
            "botonActualizar"
        ),
    o3:
        document.getElementById(
            "valorO3"
        ),
    temperatura:
        document.getElementById(
            "valorTemperatura"
        ),
    rssi:
        document.getElementById(
            "valorRssi"
        ),
    actualizado:
        document.getElementById(
            "ultimaActualizacion"
        ),
    tabla:
        document.getElementById(
            "tablaMedidas"
        ),
    sinDatos:
        document.getElementById(
            "sinDatos"
        ),
    grafica:
        document.getElementById(
            "grafica"
        )
};

/**
 * --------------------
 * Diseño lógico: iniciar() -->
 * Descripción: comprueba servidor, carga catálogos, actualiza datos y programa refresco.
 * --------------------
 */
async function iniciar() {

    ui.boton.addEventListener(
        "click",
        actualizar
    );

    try {

        await LogicaFake.health();

        ui.estado.textContent =
            "Servidor conectado";

        ui.estado.className =
            "estado ok";

        await cargarCatalogos();
        await actualizar();

        setInterval(
            actualizar,
            5000
        );

    } catch (error) {

        mostrarError(
            error instanceof Error
                ? error.message
                : String(error)
        );
    }
}

/**
 * --------------------
 * Diseño lógico: cargarCatalogos() -->
 * Descripción: obtiene dispositivos/tipos y rellena los selectores de filtros.
 * --------------------
 */
async function cargarCatalogos() {

    const [
        dispositivos,
        tipos
    ] = await Promise.all([
        LogicaFake.listarDispositivos(),
        LogicaFake.listarTiposMedida()
    ]);

    for (const d of dispositivos) {

        const option =
            document.createElement(
                "option"
            );

        option.value =
            d.dispositivoId;

        option.textContent =
            d.nombre;

        ui.dispositivo.appendChild(
            option
        );
    }

    for (const t of tipos) {

        const option =
            document.createElement(
                "option"
            );

        option.value =
            t.tipoMedidaId;

        option.textContent =
            `${t.nombre} (${t.unidad})`;

        ui.tipo.appendChild(
            option
        );
    }
}

/**
 * --------------------
 * Diseño lógico: actualizar() -->
 * Descripción: consulta medidas filtradas y refresca todos los componentes visuales.
 * --------------------
 */
async function actualizar() {

    try {

        const filtros = {
            dispositivoId:
                ui.dispositivo.value,
            tipoMedidaId:
                ui.tipo.value,
            desde:
                fechaSql(ui.desde.value),
            hasta:
                fechaSql(ui.hasta.value)
        };

        const medidas =
            await LogicaFake.listarMedidas(
                filtros
            );

        pintarResumen(medidas);
        pintarTabla(medidas);
        pintarGrafica(medidas);

        ui.actualizado.textContent =
            "Actualizado "
            + new Date()
                .toLocaleTimeString();

        ui.estado.textContent =
            "Servidor conectado";

        ui.estado.className =
            "estado ok";

    } catch (error) {

        mostrarError(
            error instanceof Error
                ? error.message
                : String(error)
        );
    }
}

/**
 * --------------------
 * Diseño lógico: valor: Text --> fechaSql() --> fecha: Text
 * Descripción: transforma datetime-local al formato SQL esperado por la API.
 * --------------------
 */
function fechaSql(valor) {

    if (!valor) {
        return "";
    }

    const fecha = valor.replace("T", " ");

    return fecha.length === 16
        ? fecha + ":00"
        : fecha.slice(0, 19);
}

/**
 * --------------------
 * Diseño lógico: medidas: [MedidaVista] --> pintarResumen() -->
 * Descripción: muestra último O3, temperatura y RSSI.
 * --------------------
 */
function pintarResumen(medidas) {

    const ultimaO3 =
        medidas.find(
            m => Number(m.tipoMedidaId) === 14
        );

    const ultimaTemperatura =
        medidas.find(
            m => Number(m.tipoMedidaId) === 12
        );

    const ultima =
        medidas[0];

    ui.o3.textContent =
        ultimaO3
            ? ultimaO3.valor
            : "—";

    ui.temperatura.textContent =
        ultimaTemperatura
            ? ultimaTemperatura.valor
            : "—";

    ui.rssi.textContent =
        ultima
            ? ultima.rssi
            : "—";
}

/**
 * --------------------
 * Diseño lógico: medidas: [MedidaVista] --> pintarTabla() -->
 * Descripción: reconstruye el histórico escapando los valores antes de insertarlos.
 * --------------------
 */
function pintarTabla(medidas) {

    ui.tabla.innerHTML = "";

    ui.sinDatos.hidden =
        medidas.length !== 0;

    for (const medida of medidas) {

        const tr =
            document.createElement("tr");

        const fecha =
            new Date(
                medida.fechaHora
            );

        tr.innerHTML = `
            <td>${escapar(
                Number.isNaN(fecha.getTime())
                    ? medida.fechaHora
                    : fecha.toLocaleString()
            )}</td>
            <td>${escapar(
                medida.dispositivo
            )}</td>
            <td>${escapar(
                medida.tipoMedida
            )}</td>
            <td>
                <strong>${escapar(
                    medida.valor
                )}</strong>
                ${escapar(
                    medida.unidad
                )}
            </td>
            <td>${escapar(
                medida.rssi
            )} dBm</td>
        `;

        ui.tabla.appendChild(tr);
    }
}

/**
 * --------------------
 * Diseño lógico: medidas: [MedidaVista] --> pintarGrafica() -->
 * Descripción: representa hasta 50 valores del tipo seleccionado en un canvas.
 * --------------------
 */
function pintarGrafica(medidas) {

    const canvas =
        ui.grafica;

    const ctx =
        canvas.getContext("2d");

    const rect =
        canvas.getBoundingClientRect();

    const escala =
        window.devicePixelRatio || 1;

    canvas.width =
        Math.max(
            600,
            Math.floor(
                rect.width * escala
            )
        );

    const alto =
        Math.max(
            220,
            Math.round(rect.height || 340)
        );

    canvas.height =
        Math.floor(
            alto * escala
        );

    ctx.scale(
        escala,
        escala
    );

    const ancho =
        canvas.width / escala;

    ctx.clearRect(
        0,
        0,
        ancho,
        alto
    );

    const seleccion =
        medidas
            .filter(
                m =>
                    Number(m.tipoMedidaId)
                    === Number(
                        ui.tipo.value || 14
                    )
            )
            .slice(0, 50)
            .reverse();

    ctx.strokeStyle =
        "#dde3e8";

    ctx.lineWidth = 1;

    for (
        let y = 40;
        y <= alto - 40;
        y += 60
    ) {
        ctx.beginPath();
        ctx.moveTo(45, y);
        ctx.lineTo(ancho - 20, y);
        ctx.stroke();
    }

    if (seleccion.length < 2) {

        ctx.fillStyle =
            "#65717d";

        ctx.font =
            "14px system-ui";

        ctx.fillText(
            "Todavía no hay suficientes datos para dibujar la evolución.",
            50,
            alto / 2
        );

        return;
    }

    const valores =
        seleccion.map(
            m => Number(m.valor)
        );

    let minimo =
        Math.min(...valores);

    let maximo =
        Math.max(...valores);

    if (minimo === maximo) {
        minimo -= 1;
        maximo += 1;
    }

    const izquierda = 50;
    const derecha = ancho - 25;
    const arriba = 30;
    const abajo = alto - 40;

    ctx.strokeStyle =
        "#2d6cdf";

    ctx.lineWidth = 2.5;

    ctx.beginPath();

    seleccion.forEach(
        (medida, indice) => {

            const x =
                izquierda
                + (
                    indice
                    / (
                        seleccion.length - 1
                    )
                )
                * (
                    derecha - izquierda
                );

            const y =
                abajo
                - (
                    (
                        Number(medida.valor)
                        - minimo
                    )
                    / (
                        maximo - minimo
                    )
                )
                * (
                    abajo - arriba
                );

            if (indice === 0) {
                ctx.moveTo(x, y);
            } else {
                ctx.lineTo(x, y);
            }
        }
    );

    ctx.stroke();

    ctx.fillStyle =
        "#65717d";

    ctx.font =
        "12px system-ui";

    ctx.fillText(
        String(maximo),
        5,
        arriba + 5
    );

    ctx.fillText(
        String(minimo),
        5,
        abajo
    );
}

/**
 * --------------------
 * Diseño lógico: valor: Text --> escapar() --> texto_seguro: Text
 * Descripción: escapa caracteres HTML para evitar interpretar datos como marcado.
 * --------------------
 */
function escapar(valor) {

    return String(
        valor ?? ""
    )
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

/**
 * --------------------
 * Diseño lógico: mensaje: Text --> mostrarError() -->
 * Descripción: registra el error y actualiza el estado visual de conexión.
 * --------------------
 */
function mostrarError(mensaje) {

    console.error(mensaje);

    ui.estado.textContent =
        "Error de comunicación";

    ui.estado.className =
        "estado error";
}

iniciar();
