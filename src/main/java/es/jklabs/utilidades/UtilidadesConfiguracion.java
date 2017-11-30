package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.ServerConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UtilidadesConfiguracion {

    private static final Logger LOG = Logger.getLogger();

    private UtilidadesConfiguracion() {

    }

    public static Configuracion loadConfig() {
        ObjectMapper mapper = new ObjectMapper();
        Configuracion configuracion = null;
        try {
            configuracion = mapper.readValue(new File("config.json"), Configuracion.class);
        } catch (FileNotFoundException e) {
            LOG.info("Fichero de configuracion no encontrado", e);
        } catch (IOException e) {
            LOG.error("Error de lectura del fichero de configuracion", e);
        }
        return configuracion;
    }

    public static void guardarConfiguracion(Configuracion configuracion) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("config.json"), configuracion);
        } catch (IOException e) {
            LOG.error("Guardar configuracion", e);
        }
    }

    public static int getIdCarpeta(Configuracion configuracion) {
        ServerConfig serverConfig = configuracion.getServerConfig();
        int retorno = serverConfig.getIndexCarpeta();
        serverConfig.setIndexCarpeta(retorno + 1);
        return retorno;
    }

    public static int getIdServidor(Configuracion configuracion) {
        ServerConfig serverConfig = configuracion.getServerConfig();
        int retorno = serverConfig.getIndexServidor();
        serverConfig.setIndexServidor(retorno + 1);
        return retorno;
    }

    public static void guardarServidores(ServerConfig serverConfig, File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, serverConfig);
        } catch (IOException e) {
            LOG.error("Guardar servidores", e);
        }
    }
}
