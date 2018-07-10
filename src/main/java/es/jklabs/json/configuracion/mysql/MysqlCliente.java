package es.jklabs.json.configuracion.mysql;

import java.io.Serializable;

public class MysqlCliente implements Serializable {

    private static final long serialVersionUID = 3437103382209777372L;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
