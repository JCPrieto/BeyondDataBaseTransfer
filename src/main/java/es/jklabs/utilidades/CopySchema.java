package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CopySchema extends SwingWorker<Void, Void> {

    private static final String COPIAR_ESQUEMA = "copiar.esquema";
    private static final Pattern NOMBRE_ESQUEMA_VALIDO = Pattern.compile("[A-Za-z0-9_$]+");
    private final Servidor sOrigen;
    private final Servidor sDestino;
    private final String esquema;
    private final MainUI parent;
    private final MysqlCliente mysqlCliente;
    private final boolean limparEsquema;

    public CopySchema(MainUI parent, MysqlCliente mysqlCliente, Servidor sOrigen, Servidor sDestino, String esquema,
                      boolean limparEsquema) {
        this.parent = parent;
        this.mysqlCliente = mysqlCliente;
        this.sOrigen = sOrigen;
        this.sDestino = sDestino;
        this.esquema = esquema;
        this.limparEsquema = limparEsquema;
    }

    private static boolean isEsquemaValido(String esquema) {
        return esquema != null && NOMBRE_ESQUEMA_VALIDO.matcher(esquema).matches();
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        if (!isEsquemaValido(esquema)) {
            Growls.mostrarAviso(COPIAR_ESQUEMA, "nombre.esquema.invalido");
            return null;
        }
        int count = 1;
        boolean origenOk = false;
        Path backup = null;
        try {
            backup = Files.createTempFile("bddt-" + esquema + "-", ".sql");
            try (OutputStream fos = Files.newOutputStream(backup)) {
                setProgress(count++);
                Process p = new ProcessBuilder(getMysqlDumpArgs()).start();
                origenOk = crearBackUp(fos, p);
            } catch (InterruptedException e) {
                Growls.mostrarError(COPIAR_ESQUEMA, "fallo.realizar.backup", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Growls.mostrarError(COPIAR_ESQUEMA, "fallo.realizar.backup", e);
            }
            if (origenOk) {
                restaurarBackUp(backup, count);
            }
        } catch (IOException e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.realizar.backup", e);
        } finally {
            eliminarBackUpTemporal(backup);
        }
        return null;
    }

    private void restaurarBackUp(Path backup, int count) {
        if (!crearEsquemaDestino(count++)) {
            return;
        }
        try (InputStream fis = Files.newInputStream(backup)) {
            setProgress(count++);
            Process p = new ProcessBuilder(getMysqlArgs()).start();
            restautarBackUp(fis, p);
            setProgress(count);
        } catch (InterruptedException e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
        }
    }

    private boolean crearEsquemaDestino(int count) {
        try {
            setProgress(count);
            Process p = new ProcessBuilder(getCrearEsquemaArgs()).start();
            List<String> errores = Collections.synchronizedList(new ArrayList<>());
            Thread errorReader = leerErroresAsync(p, errores);
            int exitCode = waitForProcess(p, errorReader);
            return procesoCorrecto(p, exitCode, errores);
        } catch (InterruptedException e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
        }
        return false;
    }

    private void eliminarBackUpTemporal(Path backup) {
        if (backup != null) {
            try {
                Files.deleteIfExists(backup);
            } catch (IOException e) {
                Growls.mostrarError(COPIAR_ESQUEMA, "eliminar.archivo.temporal");
            }
        }
    }

    private void restautarBackUp(InputStream fis, Process p) throws InterruptedException {
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (OutputStream os = p.getOutputStream()) {
            byte[] buffer = new byte[1000];
            int leido = fis.read(buffer);
            while (leido > 0) {
                os.write(buffer, 0, leido);
                leido = fis.read(buffer);
            }
            os.flush();
        } catch (Exception e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
            p.destroyForcibly();
            return;
        }
        int exitCode = waitForProcess(p, errorReader);
        boolean correcto = procesoCorrecto(p, exitCode, errores);
        if (correcto) {
            Growls.mostrarInfo(COPIAR_ESQUEMA, "copia.realizada.exito");
        }
    }

    private boolean crearBackUp(OutputStream fos, Process p) throws IOException, InterruptedException {
        List<String> errores = Collections.synchronizedList(new ArrayList<>());
        Thread errorReader = leerErroresAsync(p, errores);
        try (InputStream is = p.getInputStream()) {
            byte[] buffer = new byte[1000];
            int leido = is.read(buffer);
            while (leido > 0) {
                fos.write(buffer, 0, leido);
                leido = is.read(buffer);
            }
        } catch (Exception e) {
            Growls.mostrarError(COPIAR_ESQUEMA, "fallo.realizar.backup", e);
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
        }, "copy-schema-error-reader");
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
            if (s.contains("[Warning]")) {
                Logger.aviso(s);
            } else {
                Growls.mostrarError(COPIAR_ESQUEMA, s, true);
                correcto = false;
            }
        }
        if (exitCode != 0) {
            Growls.mostrarError(COPIAR_ESQUEMA, "Proceso mysql finalizado con codigo " + exitCode, true);
            correcto = false;
        }
        p.destroy();
        return correcto;
    }

    private List<String> getMysqlDumpArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlDumpPath());
        addCommonsArguments(args, sOrigen);
        args.add("--quick");
        args.add("--max_allowed_packet=2048M");
        args.add("--single-transaction");
        args.add("--events");
        args.add("--routines");
        args.add("--triggers");
        args.add("--databases");
        args.add(esquema);
        if (limparEsquema) {
            args.add("--add-drop-database");
        }
        return args;
    }

    private void addCommonsArguments(List<String> args, Servidor servidor) {
        args.add("-h");
        args.add(servidor.getIp());
        args.add("-P");
        args.add(String.valueOf(servidor.getPuerto()));
        args.add("-u" + servidor.getServidorBBDD().getUsuario());
        args.add("-p" + UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()));
    }

    private List<String> getMysqlArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlPath());
        addCommonsArguments(args, sDestino);
        args.add(esquema);
        return args;
    }

    private List<String> getCrearEsquemaArgs() {
        List<String> args = new ArrayList<>();
        args.add(getMysqlPath());
        addCommonsArguments(args, sDestino);
        args.add("-e");
        args.add("CREATE DATABASE IF NOT EXISTS `" + esquema + "`");
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
