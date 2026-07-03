package es.jklabs.utilidades;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilidadesEncryptacionTest {

    @Test
    public void cifraYDescifraConFormatoVersionado() {
        String encrypted = UtilidadesEncryptacion.encrypt("password");

        assertTrue(encrypted.startsWith("v2:"));
        assertEquals("password", UtilidadesEncryptacion.decrypt(encrypted));
    }

    @Test
    public void usaIvAleatorioParaElMismoTextoPlano() {
        String first = UtilidadesEncryptacion.encrypt("password");
        String second = UtilidadesEncryptacion.encrypt("password");

        assertNotEquals(first, second);
        assertEquals("password", UtilidadesEncryptacion.decrypt(first));
        assertEquals("password", UtilidadesEncryptacion.decrypt(second));
    }

    @Test
    public void descifraFormatoLegacySinPrefijoParaMigracionTransparente() {
        assertEquals("password", UtilidadesEncryptacion.decrypt("fsEFaskVhrTFYdCj99a55Q=="));
    }
}
