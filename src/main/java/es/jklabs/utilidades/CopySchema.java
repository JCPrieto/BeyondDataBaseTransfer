package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.utilidades.enumeradores.MetodoLoggin;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.FileSystemFile;

import javax.swing.*;
import java.io.IOException;

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
        try (SSHClient ssh = new SSHClient()) {
            //Conexion
            ssh.loadKnownHosts();
            ssh.connect(sOrigen.getIp(), sOrigen.getPuerto());
            if (sOrigen.getMetodoLoggin() == MetodoLoggin.CONTRASENA) {
                ssh.authPassword(sOrigen.getUsuario(), UtilidadesEncryptacion.decrypt(sOrigen.getPassword()));
            } else if (sOrigen.getMetodoLoggin() == es.jklabs.json.utilidades.enumeradores.MetodoLoggin.KEY_FILE) {
                ssh.authPublickey(sOrigen.getUsuario(), sOrigen.getKeyUrl());
            }
            Session session = ssh.startSession();
            setProgress(count++);
            //Crear dump
            Session.Command cmd = session.exec("mysqldump -u " + sOrigen.getServidorBBDD().getUsuario() + " -p"
                    + UtilidadesEncryptacion.decrypt(sOrigen.getServidorBBDD().getPassword()) + " --triggers " +
                    "--routines " + esquema + " > " + esquema + ".sql");
            LOG.info(IOUtils.readFully(cmd.getInputStream()).toString());
            cmd.join();
            LOG.info("Fin de ejecucion del comando " + cmd.getExitStatus());
            setProgress(count++);
            //Descargar dump
            ssh.newSCPFileTransfer().download(esquema + ".sql", new FileSystemFile("/home/juanky/"));
            setProgress(count++);
        } catch (IOException e) {
            LOG.error("Copiar esquema", e);
        }
        try (SSHClient ssh = new SSHClient()) {
            //Conectar con el destino
            ssh.loadKnownHosts();
            ssh.connect(sDestino.getIp(), sDestino.getPuerto());
            if (sDestino.getMetodoLoggin() == MetodoLoggin.CONTRASENA) {
                ssh.authPassword(sDestino.getUsuario(), UtilidadesEncryptacion.decrypt(sDestino.getPassword()));
            } else if (sDestino.getMetodoLoggin() == MetodoLoggin.KEY_FILE) {
                ssh.authPublickey(sDestino.getUsuario(), sDestino.getKeyUrl());
            }
            Session session = ssh.startSession();
            setProgress(count++);
            //Subir dump
            String src = "/home/juanky/" + esquema + ".sql";
            ssh.newSCPFileTransfer().upload(new FileSystemFile(src), "/home/" + sDestino.getUsuario() + "/");
            setProgress(count++);
            Session.Command cmd = session.exec("mysqldump -u " + sDestino.getServidorBBDD().getUsuario() + " -p"
                    + UtilidadesEncryptacion.decrypt(sDestino.getServidorBBDD().getPassword()) + " --triggers " +
                    "--routines " + esquema + " < " + esquema + ".sql");
            LOG.info(IOUtils.readFully(cmd.getInputStream()).toString());
            cmd.join();
            LOG.info("Fin de ejecucion del comando " + cmd.getExitStatus());
            setProgress(count);

        } catch (IOException e) {
            LOG.error("Copiar esquema", e);
        }
        return null;
    }

    @Override
    public void done() {
        parent.getBtnAceptar().setEnabled(true);
        parent.setCursor(null); //turn off the wait cursor
    }
}
