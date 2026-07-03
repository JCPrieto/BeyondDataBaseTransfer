package es.jklabs.utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class UtilidadesFichero {

    static final String HOME = System.getProperty("user.home");
    static final String BEYOND_DATA_BASE_TRANSFER_FOLDER = ".BeyondDataBaseTransfer";
    public static final String SEPARADOR = FileSystems.getDefault().getSeparator();
    static final String CONFIG_DIR_PROPERTY = "beyond.database.transfer.config.dir";
    static final String LOGS_DIR_PROPERTY = "beyond.database.transfer.logs.dir";

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

    public static Path getLogDir() {
        String logsDir = System.getProperty(LOGS_DIR_PROPERTY);
        if (logsDir != null && !logsDir.trim().isEmpty()) {
            return Paths.get(logsDir);
        }
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            String base = System.getenv("LOCALAPPDATA");
            if (base == null || base.trim().isEmpty()) {
                base = System.getenv("APPDATA");
            }
            if (base == null || base.trim().isEmpty()) {
                base = HOME;
            }
            return Paths.get(base, Constantes.NOMBRE_APP, "logs");
        }
        if (osName.contains("mac")) {
            return Paths.get(HOME, "Library", "Application Support", Constantes.NOMBRE_APP, "logs");
        }
        return Paths.get(HOME, ".local", "share", Constantes.NOMBRE_APP, "logs");
    }

    public static void createLogFolder() {
        try {
            Files.createDirectories(getLogDir());
        } catch (IOException e) {
            System.err.println("No se pudo crear la carpeta de logs: " + e.getMessage());
        }
    }
}
