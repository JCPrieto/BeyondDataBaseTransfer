package es.jklabs.gui;

import es.jklabs.json.configuracion.Configuracion;

import javax.swing.*;

public class MainUI extends JFrame {

    private final Configuracion configuracion;

    public MainUI(Configuracion configuracion) {
        this.configuracion = configuracion;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }
}
