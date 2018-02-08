package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.utilidades.enumeradores.MetodoLoggin;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.FileSystemFile;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class CopySchema extends SwingWorker<Void, Void> {

    private static final Logger LOG = Logger.getLogger();

    private final Servidor sOrigen;
    private final Servidor sDestino;
    private final String esquema;
    private final MainUI parent;

    public CopySchema(MainUI parent, Servidor sOrigen, Servidor sDestino, String esquema) {
        this.parent = parent;
        this.sOrigen = sOrigen;
        this.sDestino = sDestino;
        this.esquema = esquema;
    }

    @Override
    protected Void doInBackground() {
        setProgress(0);
        int count = 1;
        boolean origenOk = false;
        try (SSHClient ssh = new SSHClient()) {
            //Conexion
            ssh.addHostKeyVerifier((hostname, port, key) -> true);
            ssh.connect(sOrigen.getIp(), sOrigen.getPuerto());
            if (sOrigen.getMetodoLoggin() == MetodoLoggin.CONTRASENA) {
                ssh.authPassword(sOrigen.getUsuario(), Objects.requireNonNull(UtilidadesEncryptacion.decrypt(sOrigen.getPassword())));
            } else if (sOrigen.getMetodoLoggin() == es.jklabs.json.utilidades.enumeradores.MetodoLoggin.KEY_FILE) {
                ssh.authPublickey(sOrigen.getUsuario(), sOrigen.getKeyUrl());
            }
            Session session = ssh.startSession();
            setProgress(count++);
            //Crear dump
            session.exec("mysqldump -u " + sOrigen.getServidorBBDD().getUsuario() + " -p"
                    + UtilidadesEncryptacion.decrypt(sOrigen.getServidorBBDD().getPassword()) + " --triggers " +
                    "--routines " + esquema + " > " + esquema + ".sql").join();
            setProgress(count++);
            //Descargar dump
            ssh.newSCPFileTransfer().download(esquema + ".sql", System.getProperty("java.io.tmpdir"));
            setProgress(count++);
            session = ssh.startSession();
            session.exec("rm " + esquema + ".sql").join();
            setProgress(count++);
            origenOk = true;
        } catch (Exception e) {
            LOG.error("Copiar esquema", e);
        }
        if (origenOk) {
            try (SSHClient ssh = new SSHClient()) {
                //Conectar con el destino
                ssh.addHostKeyVerifier((hostname, port, key) -> true);
                ssh.connect(sDestino.getIp(), sDestino.getPuerto());
                if (sDestino.getMetodoLoggin() == MetodoLoggin.CONTRASENA) {
                    ssh.authPassword(sDestino.getUsuario(), Objects.requireNonNull(UtilidadesEncryptacion.decrypt(sDestino.getPassword())));
                } else if (sDestino.getMetodoLoggin() == MetodoLoggin.KEY_FILE) {
                    ssh.authPublickey(sDestino.getUsuario(), sDestino.getKeyUrl());
                }
                Session session = ssh.startSession();
                setProgress(count++);
                //Subir dump
                String src = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + esquema + ".sql";
                if (Objects.equals(sDestino.getUsuario(), "root")) {
                    ssh.newSCPFileTransfer().upload(new FileSystemFile(src), "/" + sDestino.getUsuario() + "/");
                } else {
                    ssh.newSCPFileTransfer().upload(new FileSystemFile(src), "/home/" + sDestino.getUsuario() + "/");
                }
                setProgress(count++);
                File file = new File(src);
                file.delete();
                setProgress(count++);
                session.exec("mysql -u " + sDestino.getServidorBBDD().getUsuario() + " -p"
                        + UtilidadesEncryptacion.decrypt(sDestino.getServidorBBDD().getPassword()) + " " + esquema + " < " + esquema + ".sql").join();
                setProgress(count++);
                session = ssh.startSession();
                session.exec("rm " + esquema + ".sql").join();
                setProgress(count);
                parent.getTrayIcon().displayMessage(esquema, "Copia realizada con éxito", TrayIcon
                        .MessageType.INFO);
            } catch (Exception e) {
                LOG.error("Copiar esquema", e);
            }
        }
        return null;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
