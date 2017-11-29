package es.jklabs.gui;

import es.jklabs.json.configuracion.Configuracion;

import javax.swing.*;

public class MainUI extends JFrame {

    private Configuracion configuracion;

    public MainUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public MainUI(Configuracion configuracion) {
        this();
        this.configuracion = configuracion;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }
}
