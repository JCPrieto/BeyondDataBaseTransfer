package es.jklabs.utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

    private static final int LOG_ROTATION_SIZE_BYTES = 5 * 1024 * 1024;
    private static final int LOG_ROTATION_COUNT = 3;
    private static final String LOG_DATE = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String LOG_PATTERN = "log_%g_" + LOG_DATE + ".log";
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static Logger logger;

    private Logger() {
        FileHandler fh;
        try {
            UtilidadesFichero.createLogFolder();
            eliminarLogsVacios();
            fh = new FileHandler(UtilidadesFichero.getLogDir().resolve(LOG_PATTERN).toString(),
                    LOG_ROTATION_SIZE_BYTES, LOG_ROTATION_COUNT, true);
            LOG.addHandler(fh);
            LOG.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            fh.setLevel(Level.ALL);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Crear archivo logs", e);
        }
    }

    public static void eliminarLogsVacios() {
        File[] logs = UtilidadesFichero.getLogDir().toFile().listFiles();
        if (logs != null) {
            Arrays.stream(logs)
                    .filter(file -> file.isFile() && file.getName().endsWith(".log"))
                    .forEach(Logger::eliminarLogVacio);
        }
    }

    private static void eliminarLogVacio(File file) {
        try {
            Path path = file.toPath();
            if (Files.size(path) == 0) {
                Files.delete(path);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Lectura logs", e);
        }
    }

    public static void error(String mensaje) {
        LOG.log(Level.SEVERE, mensaje);
    }

    static void aviso(String mensaje) {
        LOG.log(Level.WARNING, mensaje);
    }

    public static void init() {
        if (logger == null) {
            logger = new Logger();
        }
    }

    public static void error(Exception e) {
        LOG.log(Level.SEVERE, null, e);
    }

    static void info(String mensaje, Throwable e) {
        LOG.log(Level.INFO, mensaje, e);
    }

    public static void error(String mensaje, Throwable e) {
        LOG.log(Level.SEVERE, Mensajes.getError(mensaje), e);
    }

    public static void info(String mensaje) {
        LOG.log(Level.INFO, mensaje);
    }
}
