/*
 * Archivo: AppInstrumentedTest.java
 * Descripción: test instrumentado mínimo que verifica la identidad del paquete instalado.
 * Copyright: 2026 Joan (uso académico PBIO - UPV)
 * Fecha: 2026-10-01
 * Autor: Joan
 * Aportación: verificación instrumentada de la identidad de la aplicación instalada.
 */

package org.jordi.prueba2025;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AppInstrumentedTest {

    /**
     * --------------------
     * Diseño lógico: aplicacionInstalada --> comprobarPaquete() --> VoF
     * Criterio: el package desplegado debe ser org.jordi.prueba2025.
     * --------------------
     */
    @Test
    public void paqueteDeLaAplicacionEsElEsperado() {
        Context appContext =
                InstrumentationRegistry
                        .getInstrumentation()
                        .getTargetContext();

        assertEquals(
                "org.jordi.prueba2025",
                appContext.getPackageName()
        );
    }
}
