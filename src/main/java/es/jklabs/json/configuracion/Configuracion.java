package es.jklabs.json.configuracion;

import es.jklabs.json.configuracion.server.ServerConfig;

public class Configuracion {

    ServerConfig serverConfig;

    public Configuracion() {
        serverConfig = new ServerConfig();
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

}
