package es.jklabs.utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class UtilidadesFichero {

    static final String HOME = System.getProperty("user.home");
    static final String BEYOND_DATA_BASE_TRANSFER_FOLDER = ".BeyondDataBaseTransfer";
    public static final String SEPARADOR = System.getProperty("file.separator");
    private static final Logger LOG = Logger.getLogger();

    private UtilidadesFichero() {

    }

    static void createBaseFolder() {
        File base = new File(HOME + SEPARADOR + BEYOND_DATA_BASE_TRANSFER_FOLDER);
        if (!base.exists()) {
            try {
                Files.createDirectory(FileSystems.getDefault().getPath(HOME + SEPARADOR + BEYOND_DATA_BASE_TRANSFER_FOLDER));
            } catch (IOException e) {
                LOG.error("Crear carpeta base", e);
            }
        }
    }
}
