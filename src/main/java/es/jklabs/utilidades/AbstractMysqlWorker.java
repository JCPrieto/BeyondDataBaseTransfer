package es.jklabs.utilidades;

import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import java.util.List;

public abstract class AbstractMysqlWorker extends SwingWorker<Void, Void> {

    protected void addCommonsArguments(List<String> args, Servidor servidor) {
        args.add("-h");
        args.add(servidor.getIp());
        args.add("-P");
        args.add(String.valueOf(servidor.getPuerto()));
        args.add("-u" + servidor.getServidorBBDD().getUsuario());
        args.add("-p" + UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()));
    }
}
