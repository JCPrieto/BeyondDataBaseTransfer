package es.jklabs.json.configuracion.server;

import java.util.ArrayList;
import java.util.List;

public class Carpeta {

    String nombre;
    boolean sistema;
    List<Carpeta> carpetas;
    List<Servidor> servidores;

    public Carpeta() {
        carpetas = new ArrayList<>();
        servidores = new ArrayList<>();
    }

    public Carpeta(String nombre, boolean sistema) {
        this();
        this.nombre = nombre;
        this.sistema = sistema;
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

    public List<Servidor> getServidores() {
        return servidores;
    }

    public void setServidores(List<Servidor> servidores) {
        this.servidores = servidores;
    }
}
