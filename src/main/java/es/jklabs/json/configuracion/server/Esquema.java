package es.jklabs.json.configuracion.server;

import java.util.Objects;

public class Esquema {

    String nombre;

    public Esquema() {
    }

    public Esquema(String esquema) {
        nombre = esquema;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Esquema)) return false;
        Esquema esquema = (Esquema) o;
        return Objects.equals(getNombre(), esquema.getNombre());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getNombre());
    }
}
