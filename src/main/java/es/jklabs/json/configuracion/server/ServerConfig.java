package es.jklabs.json.configuracion.server;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {

    List<Carpeta> carpetas;

    public ServerConfig() {
        carpetas = new ArrayList<>();
        carpetas.add(new Carpeta("Raiz", true));
    }

    public List<Carpeta> getCarpetas() {
        return carpetas;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.carpetas = carpetas;
    }
}
