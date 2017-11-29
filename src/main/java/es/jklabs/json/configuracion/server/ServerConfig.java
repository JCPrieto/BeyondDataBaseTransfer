package es.jklabs.json.configuracion.server;

public class ServerConfig {

    Carpeta raiz;
    int indexCarpeta;
    int indexServidor;

    public ServerConfig() {
        raiz = new Carpeta("Raiz", true, 1);
        indexCarpeta = 1;
        indexServidor = 1;
    }

    public Carpeta getRaiz() {
        return raiz;
    }

    public void setRaiz(Carpeta raiz) {
        this.raiz = raiz;
    }

    public int getIndexCarpeta() {
        return indexCarpeta;
    }

    public void setIndexCarpeta(int indexCarpeta) {
        this.indexCarpeta = indexCarpeta;
    }

    public int getIndexServidor() {
        return indexServidor;
    }

    public void setIndexServidor(int indexServidor) {
        this.indexServidor = indexServidor;
    }
}
