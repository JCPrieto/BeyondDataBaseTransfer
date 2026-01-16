package es.jklabs.json.configuracion.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Carpeta implements Serializable {

    private static final long serialVersionUID = -3584563633306974634L;
    private int id;
    private String nombre;
    private boolean sistema;
    private List<Carpeta> carpetas;
    private List<Servidor> servidores;

    public Carpeta() {
        carpetas = new ArrayList<>();
        servidores = new ArrayList<>();
    }

    public Carpeta(String nombre, boolean sistema, int id) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.sistema = sistema;
    }

    public Carpeta(int id) {
        this();
        this.id = id;
    }

    public Carpeta(int id, Carpeta origen) {
        this(origen.getNombre(), false, id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isSistema() {
        return sistema;
    }

    public void setSistema(boolean sistema) {
        this.sistema = sistema;
    }

    public List<Carpeta> getCarpetas() {
        return carpetas;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.carpetas = carpetas;
    }

    public List<Servidor> getServidores() {
        return servidores;
    }

    public void setServidores(List<Servidor> servidores) {
        this.servidores = servidores;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carpeta)) return false;

        Carpeta carpeta = (Carpeta) o;

        return getId() == carpeta.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
