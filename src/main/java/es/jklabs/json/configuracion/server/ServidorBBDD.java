package es.jklabs.json.configuracion.server;

import java.io.Serializable;

public class ServidorBBDD implements Serializable {

    private static final long serialVersionUID = 930490770186059126L;
    String usuario;
    String password;

    public ServidorBBDD() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
