package es.jklabs.gui.configuracion.panel;

import es.jklabs.json.configuracion.Configuracion;

import javax.swing.*;

public class MysqlConfigPanel extends JPanel {

    private static final long serialVersionUID = 3500969681893444957L;
    private final Configuracion configuracion;

    public MysqlConfigPanel(Configuracion configuracion) {
        super();
        this.configuracion = configuracion;
    }
}
