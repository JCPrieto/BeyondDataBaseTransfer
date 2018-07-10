package es.jklabs.json.configuracion;

import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.ServerConfig;

import java.io.Serializable;

public class Configuracion implements Serializable {

    private static final long serialVersionUID = 816509406827367555L;
    ServerConfig serverConfig;
    MysqlCliente mysqlCliente;

    public Configuracion() {
        serverConfig = new ServerConfig();
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public MysqlCliente getMysqlCliente() {
        return mysqlCliente;
    }

    public void setMysqlCliente(MysqlCliente mysqlCliente) {
        this.mysqlCliente = mysqlCliente;
    }
}
