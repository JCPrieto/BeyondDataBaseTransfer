package es.jklabs.utilidades;

import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import java.util.List;

public abstract class AbstractMysqlWorker extends SwingWorker<Void, Void> {

    public static final String PROGRESS_DESCRIPTION_PROPERTY = "progressDescription";

    protected void addCommonsArguments(List<String> args, Servidor servidor) {
        args.add("-h");
        args.add(servidor.getIp());
        args.add("-P");
        args.add(String.valueOf(servidor.getPuerto()));
        args.add("-u" + servidor.getServidorBBDD().getUsuario());
        args.add("-p" + UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()));
    }

    protected void setProgressDescription(String descripcion) {
        firePropertyChange(PROGRESS_DESCRIPTION_PROPERTY, null, descripcion);
    }
}
