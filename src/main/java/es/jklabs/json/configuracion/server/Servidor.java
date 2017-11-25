package es.jklabs.json.configuracion.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.jklabs.json.utilidades.enumeradores.MetodoLoggin;

import java.util.List;

public class Servidor {

    String nombre;
    String ip;
    Integer puerto;
    MetodoLoggin metodoLoggin;
    String usuario;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String keyUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String keyValue;
    List<ServidorBBDD> servidoresBBDD;
    List<Esquema> servidor;

    public Servidor() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public MetodoLoggin getMetodoLoggin() {
        return metodoLoggin;
    }

    public void setMetodoLoggin(MetodoLoggin metodoLoggin) {
        this.metodoLoggin = metodoLoggin;
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

    public String getKeyUrl() {
        return keyUrl;
    }

    public void setKeyUrl(String keyUrl) {
        this.keyUrl = keyUrl;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public List<ServidorBBDD> getServidoresBBDD() {
        return servidoresBBDD;
    }

    public void setServidoresBBDD(List<ServidorBBDD> servidoresBBDD) {
        this.servidoresBBDD = servidoresBBDD;
    }

    public List<Esquema> getServidor() {
        return servidor;
    }

    public void setServidor(List<Esquema> servidor) {
        this.servidor = servidor;
    }
}
