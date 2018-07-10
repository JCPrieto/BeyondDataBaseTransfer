package es.jklabs.json.configuracion.server;

import java.io.Serializable;
import java.util.List;

public class Servidor implements Serializable {

    private static final long serialVersionUID = -6230755833251637068L;
    private int id;
    private String nombre;
    private String ip;
    private Integer puerto;
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
