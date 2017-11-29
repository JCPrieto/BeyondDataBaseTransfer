package es.jklabs.json.configuracion;

import es.jklabs.json.configuracion.server.ServerConfig;

import java.io.Serializable;

public class Configuracion implements Serializable {

    private static final long serialVersionUID = 816509406827367555L;
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
