package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CloneSchema extends AbstractMysqlWorker {

    private static final String CLONAR_ESQUEMA = "clonar.esquema";
    private static final Pattern NOMBRE_ESQUEMA_VALIDO = Pattern.compile("[A-Za-z0-9_$]+");
    private final MainUI parent;
    private final MysqlCliente mysqlCliente;
    private final Servidor servidor;
    private final String esquemaOrigen;
    private final String esquemaDestino;

    public CloneSchema(MainUI parent, MysqlCliente mysqlCliente, Servidor servidor, String esquemaOrigen,
                       String esquemaDestino) {
        this.parent = parent;
        this.mysqlCliente = mysqlCliente;
        this.servidor = servidor;
        this.esquemaOrigen = esquemaOrigen;
        this.esquemaDestino = esquemaDestino;
    }

    private static boolean esquemaNoValido(String esquema) {
        return esquema == null || !NOMBRE_ESQUEMA_VALIDO.matcher(esquema).matches();
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        if (esquemaNoValido(esquemaOrigen) || esquemaNoValido(esquemaDestino)) {
            Growls.mostrarAviso(CLONAR_ESQUEMA, "nombre.esquema.invalido");
            return null;
        }
        Path backup = null;
        try {
            backup = Files.createTempFile("bddt-clone-" + esquemaOrigen + "-", ".sql");
            try (OutputStream fos = Files.newOutputStream(backup)) {
                setProgress(1);
                Process p = new ProcessBuilder(getMysqlDumpArgs()).start();
                if (!crearBackUp(fos, p)) {
                    return null;
                }
            }
            if (!crearEsquemaDestino()) {
                return null;
            }
            if (restaurarBackUp(backup)) {
                setProgress(3);
            }
        } catch (InterruptedException e) {
            Growls.mostrarError(CLONAR_ESQUEMA, "fallo.clonar.esquema", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Growls.mostrarError(CLONAR_ESQUEMA, "fallo.clonar.esquema", e);
        } finally {
            eliminarBackUpTemporal(backup);
        }
        return null;
    }

    private boolean crearBackUp(OutputStream fos, Process p) throws InterruptedException {
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (InputStream is = p.getInputStream()) {
            is.transferTo(fos);
        } catch (Exception e) {
            Growls.mostrarError(CLONAR_ESQUEMA, "fallo.realizar.backup", e);
            p.destroyForcibly();
            return false;
        }
        int exitCode = waitForProcess(p, errorReader);
        return procesoCorrecto(p, exitCode, errores);
    }

    private boolean crearEsquemaDestino() throws IOException, InterruptedException {
        setProgress(2);
        Process p = new ProcessBuilder(getCrearEsquemaArgs()).start();
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        int exitCode = waitForProcess(p, errorReader);
        return procesoCorrecto(p, exitCode, errores);
    }

    private boolean restaurarBackUp(Path backup) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(getMysqlArgs()).start();
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (InputStream fis = Files.newInputStream(backup); OutputStream os = p.getOutputStream()) {
            fis.transferTo(os);
            os.flush();
        } catch (Exception e) {
            Growls.mostrarError(CLONAR_ESQUEMA, "fallo.restaurar.backup", e);
            p.destroyForcibly();
            return false;
        }
        int exitCode = waitForProcess(p, errorReader);
        boolean correcto = procesoCorrecto(p, exitCode, errores);
        if (correcto) {
            Growls.mostrarInfo(CLONAR_ESQUEMA, "esquema.clonado.exito");
        }
        return correcto;
    }

    private Thread leerErroresAsync(Process p, List<String> errores) {
        Thread thread = new Thread(() -> {
            try (BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                String s;
                while ((s = stdError.readLine()) != null) {
                    errores.add(s);
                }
            } catch (IOException e) {
                Logger.error("Leer salida de error del proceso", e);
            }
        }, "clone-schema-error-reader");
        thread.start();
        return thread;
    }

    private int waitForProcess(Process p, Thread errorReader) throws InterruptedException {
        try {
            int exitCode = p.waitFor();
            errorReader.join();
            return exitCode;
        } catch (InterruptedException e) {
            p.destroyForcibly();
            errorReader.interrupt();
            throw e;
        }
    }

    private boolean procesoCorrecto(Process p, int exitCode, List<String> errores) {
        boolean correcto = true;
        boolean errorNotificado = false;
        for (String s : errores) {
            if (isWarning(s)) {
                Logger.aviso(s);
            } else {
                Logger.error(s);
                if (!errorNotificado) {
                    Growls.mostrarError(CLONAR_ESQUEMA, getMensajeErrorUsuario(s));
                    errorNotificado = true;
                }
                correcto = false;
            }
        }
        if (exitCode != 0) {
            Logger.error("Proceso mysql finalizado con codigo " + exitCode);
            if (!errorNotificado) {
                Growls.mostrarError(CLONAR_ESQUEMA, "fallo.clonar.esquema");
            }
            correcto = false;
        }
        p.destroy();
        return correcto;
    }

    private String getMensajeErrorUsuario(String error) {
        if (error.contains("ERROR 1044") && error.contains("Access denied")) {
            return "fallo.clonar.esquema.permisos";
        }
        return error;
    }

    private boolean isWarning(String salidaError) {
        return salidaError.contains("[Warning]") || salidaError.startsWith("-- Warning:");
    }

    private void eliminarBackUpTemporal(Path backup) {
        if (backup != null) {
            try {
                Files.deleteIfExists(backup);
            } catch (IOException e) {
                Growls.mostrarError(CLONAR_ESQUEMA, "eliminar.archivo.temporal");
            }
        }
    }

    private List<String> getMysqlDumpArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlDumpPath());
        addCommonsArguments(args, servidor);
        args.add("--quick");
        args.add("--max_allowed_packet=2048M");
        args.add("--single-transaction");
        args.add("--routines");
        args.add("--triggers");
        args.add(esquemaOrigen);
        return args;
    }

    private List<String> getCrearEsquemaArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlPath());
        addCommonsArguments(args, servidor);
        args.add("-e");
        args.add("CREATE DATABASE IF NOT EXISTS `" + esquemaDestino + "`");
        return args;
    }

    private List<String> getMysqlArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlPath());
        addCommonsArguments(args, servidor);
        args.add(esquemaDestino);
        return args;
    }

    private String getMysqlPath() {
        return mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR + getMysqlCommand();
    }

    private String getMysqlDumpPath() {
        return mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR + getMysqlDumpCommand();
    }

    private String getMysqlCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQL_EXE : Constantes.MYSQL;
    }

    private String getMysqlDumpCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQLDUMP_EXE : Constantes.MYSQLDUMP;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
