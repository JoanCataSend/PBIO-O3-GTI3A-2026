/*
 * Archivo: ServidorUnitTest.java
 * Descripción: tests unitarios de la configuración del endpoint REST Android.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verificación automática de URLs REST utilizadas por Android.
 */

package es.upv.jcatsen.pbio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServidorUnitTest {

    /**
     * --------------------
     * Diseño lógico: urlDelServidorEsLaEsperada()
     * Descripción: prueba automáticamente el criterio indicado.
     * Criterio: Android debe apuntar al endpoint HTTPS desplegado.
     * --------------------
     */
    @Test
    public void urlDelServidorEsLaEsperada() {
        assertEquals(
                "https://jcatsen.upv.edu.es/biometria/api.php",
                LogicaFake.URL_API
        );
    }

    /**
     * --------------------
     * Diseño lógico: urlHealthEsLaEsperada()
     * Descripción: verifica el endpoint usado para diagnóstico de conectividad desde el móvil.
     * --------------------
     */
    @Test
    public void urlHealthEsLaEsperada() {
        assertEquals(
                "https://jcatsen.upv.edu.es/biometria/api.php?accion=health",
                LogicaFake.URL_HEALTH
        );
    }

    /**
     * --------------------
     * Diseño lógico: urlsUsanHttps()
     * Descripción: comprueba que tanto escritura como health usan transporte cifrado.
     * --------------------
     */
    @Test
    public void urlsUsanHttps() {
        assertTrue(LogicaFake.URL_API.startsWith("https://"));
        assertTrue(LogicaFake.URL_HEALTH.startsWith("https://"));
    }
}
