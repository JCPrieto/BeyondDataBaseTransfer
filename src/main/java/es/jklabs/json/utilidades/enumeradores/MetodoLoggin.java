package es.jklabs.json.utilidades.enumeradores;

public enum MetodoLoggin {

    CONTRASENA("Contraseña"),
    KEY_FILE("Archivo de clave rsa"),
    KEY_VALUE("Clave RSA");

    private final String descripcion;

    MetodoLoggin(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
