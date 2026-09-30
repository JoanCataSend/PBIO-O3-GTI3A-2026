package org.jordi.prueba2025;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServidorUnitTest {

    @Test
    public void urlDelServidorEsLaEsperada() {

        assertEquals(
                "https://jcatsen.upv.edu.es/biometria/api.php",
                LogicaFake.URL_API
        );
    }

    @Test
    public void urlUsaElSubdominioPbio() {

        assertTrue(
                LogicaFake.URL_API.contains(
                        "jcatsen.upv.edu.es/biometria"
                )
        );
    }
}
