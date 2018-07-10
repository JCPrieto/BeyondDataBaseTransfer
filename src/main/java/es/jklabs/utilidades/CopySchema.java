package es.jklabs.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import java.io.File;

public class CopySchema extends SwingWorker<Void, Void> {

    private static final String COPIAR_ESQUEMA = "copiar.esquema";
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
        try {
            //Conexion
            setProgress(count++);
            //Crear dump
            Runtime.getRuntime().exec("mysqldump -u " + sOrigen.getServidorBBDD().getUsuario() + " -p"
                    + UtilidadesEncryptacion.decrypt(sOrigen.getServidorBBDD().getPassword()) + " --quick " +
                    "--single-transaction --events --routines --triggers " + esquema + " > " + System.getProperty
                    ("java.io.tmpdir") + UtilidadesFichero.SEPARADOR + esquema + ".sql");
            origenOk = true;
        } catch (Exception e) {
            Growls.mostrarError(parent, COPIAR_ESQUEMA, "fallo.realizar.backup", e);
        }
        if (origenOk) {
            String src = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + esquema + ".sql";
            try {
                setProgress(count++);
                Runtime.getRuntime().exec("mysql -u " + sDestino.getServidorBBDD().getUsuario() + " -p"
                        + UtilidadesEncryptacion.decrypt(sDestino.getServidorBBDD().getPassword()) + " " + esquema +
                        " < " + src);
                setProgress(count);
                Growls.mostrarInfo(parent, COPIAR_ESQUEMA, "copia.realizada.exito");
            } catch (Exception e) {
                Growls.mostrarError(parent, COPIAR_ESQUEMA, "fallo.restaurar.backup", e);
            } finally {
                File file = new File(src);
                file.delete();
            }
        }
        return null;
    }

    @Override
    public void done() {
        parent.desbloquearPantalla();
    }
}
