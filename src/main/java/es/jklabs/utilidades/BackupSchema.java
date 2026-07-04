package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class BackupSchema extends AbstractMysqlWorker {

    private static final String CREAR_BACKUP = "crear.backup";
    private static final Pattern NOMBRE_ESQUEMA_VALIDO = Pattern.compile("[A-Za-z0-9_$]+");
    private final MainUI parent;
    private final MysqlCliente mysqlCliente;
    private final Servidor servidor;
    private final String esquema;
    private final File backup;

    public BackupSchema(MainUI parent, MysqlCliente mysqlCliente, Servidor servidor, String esquema, File backup) {
        this.parent = parent;
        this.mysqlCliente = mysqlCliente;
        this.servidor = servidor;
        this.esquema = esquema;
        this.backup = backup;
    }

    private static boolean isEsquemaValido(String esquema) {
        return esquema != null && NOMBRE_ESQUEMA_VALIDO.matcher(esquema).matches();
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        if (!isEsquemaValido(esquema)) {
            Growls.mostrarAviso(CREAR_BACKUP, "nombre.esquema.invalido");
            return null;
        }
        try (OutputStream fos = new FileOutputStream(backup)) {
            setProgress(1);
            Process p = new ProcessBuilder(getMysqlDumpArgs()).start();
            if (crearBackUp(fos, p)) {
                Growls.mostrarInfo(CREAR_BACKUP, "backup.realizado.exito");
            }
            setProgress(2);
        } catch (InterruptedException e) {
            Growls.mostrarError(CREAR_BACKUP, "fallo.realizar.backup", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Growls.mostrarError(CREAR_BACKUP, "fallo.realizar.backup", e);
        }
        return null;
    }

    private boolean crearBackUp(OutputStream fos, Process p) throws InterruptedException {
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (InputStream is = p.getInputStream()) {
            is.transferTo(fos);
        } catch (Exception e) {
            Growls.mostrarError(CREAR_BACKUP, "fallo.realizar.backup", e);
            p.destroyForcibly();
            return false;
        }
        int exitCode = waitForProcess(p, errorReader);
        return procesoCorrecto(p, exitCode, errores);
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
        }, "backup-schema-error-reader");
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
        for (String s : errores) {
            if (isWarning(s)) {
                Logger.aviso(s);
            } else {
                Growls.mostrarError(CREAR_BACKUP, s, true);
                correcto = false;
            }
        }
        if (exitCode != 0) {
            Growls.mostrarError(CREAR_BACKUP, "Proceso mysqldump finalizado con codigo " + exitCode, true);
            correcto = false;
        }
        p.destroy();
        return correcto;
    }

    private boolean isWarning(String salidaError) {
        return salidaError.contains("[Warning]") || salidaError.startsWith("-- Warning:");
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
        args.add("--databases");
        args.add(esquema);
        return args;
    }

    private String getMysqlDumpPath() {
        return mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR + getMysqlDumpCommand();
    }

    private String getMysqlDumpCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQLDUMP_EXE : Constantes.MYSQLDUMP;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
