package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CopySchema extends SwingWorker<Void, Void> {

    private static final String COPIAR_ESQUEMA = "copiar.esquema";
    private final Servidor sOrigen;
    private final Servidor sDestino;
    private final String esquema;
    private final MainUI parent;
    private final MysqlCliente mysqlCliente;

    public CopySchema(MainUI parent, MysqlCliente mysqlCliente, Servidor sOrigen, Servidor sDestino, String esquema) {
        this.parent = parent;
        this.mysqlCliente = mysqlCliente;
        this.sOrigen = sOrigen;
        this.sDestino = sDestino;
        this.esquema = esquema;
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        int count = 1;
        boolean origenOk = false;
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + esquema + ".sql")) {
            //Conexion
            setProgress(count++);
            //Crear dump
            String server = "-h " + sOrigen.getIp();
            String port = "-P " + sOrigen.getPuerto();
            String usuario = "-u" + sOrigen.getServidorBBDD().getUsuario();
            String pass = "-p" + UtilidadesEncryptacion.decrypt(sOrigen.getServidorBBDD().getPassword());
            Process p = Runtime.getRuntime().exec(mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR + Constantes
                    .MYSQLDUMP + " " + server + " " + port + " " + usuario + " " + pass + " --quick " +
                    "--max_allowed_packet=2048M --single-transaction --events --routines --triggers --databases " + esquema);
            InputStream is = p.getInputStream();
            byte[] buffer = new byte[1000];
            int leido = is.read(buffer);
            while (leido > 0) {
                fos.write(buffer, 0, leido);
                leido = is.read(buffer);
            }
            origenOk = sinErrores(p);
        } catch (Exception e) {
            Growls.mostrarError(parent, COPIAR_ESQUEMA, "fallo.realizar.backup", e);
        }
        if (origenOk) {
            String src = System.getProperty("java.io.tmpdir") + esquema + ".sql";
            boolean destinoOk = false;
            try (FileInputStream fis = new FileInputStream(src)) {
                String server = "-h " + sDestino.getIp();
                String port = "-P " + sDestino.getPuerto();
                String usuario = "-u" + sDestino.getServidorBBDD().getUsuario();
                String pass = "-p" + UtilidadesEncryptacion.decrypt(sDestino.getServidorBBDD().getPassword());
                setProgress(count++);
                Process p = Runtime.getRuntime().exec(mysqlCliente.getPath() + UtilidadesFichero.SEPARADOR +
                        Constantes.MYSQL + " " + server + " " + port + " " + usuario + " " + pass + " " + esquema);
                OutputStream os = p.getOutputStream();
                byte[] buffer = new byte[1000];
                int leido = fis.read(buffer);
                while (leido > 0) {
                    os.write(buffer, 0, leido);
                    leido = fis.read(buffer);
                }
                os.flush();
                os.close();
                destinoOk = sinErrores(p);
                if (destinoOk) {
                    setProgress(count);
                    Growls.mostrarInfo(parent, COPIAR_ESQUEMA, "copia.realizada.exito");
                }
            } catch (Exception e) {
                Growls.mostrarError(parent, COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
            } finally {
                Path paths = Paths.get(src);
                try {
                    if (destinoOk) {
                        Files.delete(paths);
                    }
                } catch (IOException e) {
                    Growls.mostrarError(parent, COPIAR_ESQUEMA, "eliminar.archivo.temporal");
                }
            }
        }
        return null;
    }

    private boolean sinErrores(Process p) throws IOException {
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));
        String s;
        boolean correcto = true;
        while ((s = stdError.readLine()) != null) {
            if (s.contains("[Warning]")) {
                Logger.aviso(s);
            } else {
                Growls.mostrarError(parent, COPIAR_ESQUEMA, s, true);
                correcto = false;
            }
        }
        return correcto;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
