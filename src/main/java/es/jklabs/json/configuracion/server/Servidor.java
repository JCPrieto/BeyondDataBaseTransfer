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
    private ServidorBBDD servidorBBDD;
    private List<Esquema> esquemas;

    public Servidor() {
    }

    public Servidor(int id) {
        this();
        this.id = id;
    }

    public Servidor(int id, Servidor s) {
        this(id);
        this.nombre = s.getNombre();
        this.ip = s.getIp();
        this.puerto = s.getPuerto();
        this.metodoLoggin = s.getMetodoLoggin();
        if (metodoLoggin == MetodoLoggin.CONTRASENA) {
            this.password = s.getPassword();

        } else if (metodoLoggin == MetodoLoggin.KEY_FILE) {
            this.keyUrl = s.getKeyUrl();

        }
        this.servidorBBDD = s.getServidorBBDD();
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

    public ServidorBBDD getServidorBBDD() {
        return servidorBBDD;
    }

    public void setServidorBBDD(ServidorBBDD servidorBBDD) {
        this.servidorBBDD = servidorBBDD;
    }

    public List<Esquema> getEsquemas() {
        return esquemas;
    }

    public void setEsquemas(List<Esquema> esquemas) {
        this.esquemas = esquemas;
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
