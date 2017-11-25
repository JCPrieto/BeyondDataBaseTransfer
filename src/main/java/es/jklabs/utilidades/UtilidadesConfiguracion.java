package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.json.configuracion.Configuracion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UtilidadesConfiguracion {

    private static Logger LOG = Logger.getLogger();

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

}
