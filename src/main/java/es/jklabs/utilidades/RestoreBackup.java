package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestoreBackup extends AbstractMysqlWorker {

    private static final String RESTAURAR_BACKUP = "restaurar.backup";
    private final MainUI parent;
    private final MysqlCliente mysqlCliente;
    private final Servidor servidor;
    private final File backup;

    public RestoreBackup(MainUI parent, MysqlCliente mysqlCliente, Servidor servidor, File backup) {
        this.parent = parent;
        this.mysqlCliente = mysqlCliente;
        this.servidor = servidor;
        this.backup = backup;
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        try (InputStream fis = new FileInputStream(backup)) {
            setProgress(1);
            Process p = new ProcessBuilder(getMysqlArgs()).start();
            if (restaurarBackUp(fis, p)) {
                Growls.mostrarInfo(RESTAURAR_BACKUP, "backup.restaurado.exito");
            }
            setProgress(2);
        } catch (InterruptedException e) {
            Growls.mostrarError(RESTAURAR_BACKUP, "fallo.restaurar.backup", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Growls.mostrarError(RESTAURAR_BACKUP, "fallo.restaurar.backup", e);
        }
        return null;
    }

    private boolean restaurarBackUp(InputStream fis, Process p) throws InterruptedException {
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (OutputStream os = p.getOutputStream()) {
            fis.transferTo(os);
            os.flush();
        } catch (Exception e) {
            Growls.mostrarError(RESTAURAR_BACKUP, "fallo.restaurar.backup", e);
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
        }, "restore-backup-error-reader");
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
            if (s.contains("[Warning]")) {
                Logger.aviso(s);
            } else {
                Logger.error(s);
                if (!errorNotificado) {
                    Growls.mostrarError(RESTAURAR_BACKUP, getMensajeErrorUsuario(s));
                    errorNotificado = true;
                }
                correcto = false;
            }
        }
        if (exitCode != 0) {
            Logger.error("Proceso mysql finalizado con codigo " + exitCode);
            if (!errorNotificado) {
                Growls.mostrarError(RESTAURAR_BACKUP, "fallo.restaurar.backup");
            }
            correcto = false;
        }
        p.destroy();
        return correcto;
    }

    private String getMensajeErrorUsuario(String error) {
        if (error.contains("ERROR 1044") && error.contains("Access denied")) {
            return "fallo.restaurar.backup.permisos";
        }
        return error;
    }

    private List<String> getMysqlArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlPath());
        addCommonsArguments(args, servidor);
        return args;
    }

    private String getMysqlPath() {
        return mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR + getMysqlCommand();
    }

    private String getMysqlCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQL_EXE : Constantes.MYSQL;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
