package es.jklabs.json.configuracion.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.jklabs.json.utilidades.enumeradores.MetodoLoggin;

import java.util.List;

public class Servidor {

    private int id;
    private String nombre;
    private String ip;
    private Integer puerto;
    private MetodoLoggin metodoLoggin;
    private String usuario;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private
    String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private
    String keyUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private
    String keyValue;
    private ServidorBBDD servidorBBDD;
    private List<Esquema> servidor;

    public Servidor() {
    }

    public Servidor(int id) {
        this.id = id;
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

    public ServidorBBDD getServidorBBDD() {
        return servidorBBDD;
    }

    public void setServidorBBDD(ServidorBBDD servidorBBDD) {
        this.servidorBBDD = servidorBBDD;
    }

    public List<Esquema> getServidor() {
        return servidor;
    }

    public void setServidor(List<Esquema> servidor) {
        this.servidor = servidor;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servidor)) return false;

        Servidor servidor = (Servidor) o;

        return getId() == servidor.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
