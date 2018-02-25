package es.jklabs.json.utilidades.enumeradores;

import java.util.Locale;
import java.util.ResourceBundle;

public enum MetodoLoggin {

    CONTRASENA("contrasena"),
    KEY_FILE("archivo.clave.rsa");

    private static ResourceBundle enumeradores = ResourceBundle.getBundle("i18n/enumeradores", Locale.getDefault());
    private final String descripcion;

    MetodoLoggin(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        if (enumeradores.containsKey(descripcion)) {
            return enumeradores.getString(descripcion);
        } else {
            return descripcion;
        }
    }
}
