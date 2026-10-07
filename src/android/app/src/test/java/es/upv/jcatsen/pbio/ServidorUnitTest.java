/*
 * Archivo: ServidorUnitTest.java
 * Descripción: tests unitarios de la configuración del endpoint REST Android.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: verificación automática de la URL REST utilizada por Android.
 */

package es.upv.jcatsen.pbio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServidorUnitTest {

    /**
     * --------------------
     * Diseño lógico: urlDelServidorEsLaEsperada() -->
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
     * Diseño lógico: urlUsaRutaBiometria() -->
     * Descripción: prueba automáticamente el criterio indicado.
     * Criterio: la URL debe utilizar la ruta /biometria del dominio principal.
     * --------------------
     */
    @Test
    public void urlUsaRutaBiometria() {

        assertTrue(
                LogicaFake.URL_API.contains(
                        "jcatsen.upv.edu.es/biometria"
                )
        );
    }
}
