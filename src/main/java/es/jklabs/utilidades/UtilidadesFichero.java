package es.jklabs.utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UtilidadesFichero {

    static final String HOME = System.getProperty("user.home");
    static final String BEYOND_DATA_BASE_TRANSFER_FOLDER = ".BeyondDataBaseTransfer";
    public static final String SEPARADOR = FileSystems.getDefault().getSeparator();
    static final String CONFIG_DIR_PROPERTY = "beyond.database.transfer.config.dir";

    private UtilidadesFichero() {

    }

    static void createBaseFolder() {
        File base = getBaseFolderPath().toFile();
        if (!base.exists()) {
            try {
                Files.createDirectories(getBaseFolderPath());
            } catch (IOException e) {
                Logger.error("Crear carpeta base", e);
            }
        }
    }

    static Path getBaseFolderPath() {
        String configDir = System.getProperty(CONFIG_DIR_PROPERTY);
        if (configDir != null && !configDir.trim().isEmpty()) {
            return Paths.get(configDir);
        }
        return Paths.get(HOME, BEYOND_DATA_BASE_TRANSFER_FOLDER);
    }
}
