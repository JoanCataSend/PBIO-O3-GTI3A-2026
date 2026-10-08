/*
 * Archivo: AppInstrumentedTest.java
 * Descripción: test instrumentado mínimo que verifica la identidad del paquete instalado.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verificación instrumentada de la identidad de la aplicación instalada.
 */

package es.upv.jcatsen.pbio;

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
     * Diseño lógico: paqueteDeLaAplicacionEsElEsperado()
     * Descripción: prueba automáticamente el criterio indicado.
     * Criterio: el package desplegado debe ser es.upv.jcatsen.pbio.
     * --------------------
     */
    @Test
    public void paqueteDeLaAplicacionEsElEsperado() {
        Context appContext =
                InstrumentationRegistry
                        .getInstrumentation()
                        .getTargetContext();

        assertEquals(
                "es.upv.jcatsen.pbio",
                appContext.getPackageName()
        );
    }
}
