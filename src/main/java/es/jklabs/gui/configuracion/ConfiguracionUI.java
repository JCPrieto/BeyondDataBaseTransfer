package es.jklabs.gui.configuracion;

import es.jklabs.json.configuracion.Configuracion;

import javax.swing.*;

public class ConfiguracionUI extends JFrame {

    private boolean crearConfiguracion;
    private Configuracion configuracion;

    public ConfiguracionUI() {

    }

    public ConfiguracionUI(Configuracion configuracion) {
        this();
        crearConfiguracion = false;
        this.configuracion = configuracion;
    }

    public ConfiguracionUI(boolean crearConfiguracion) {
        this();
        this.crearConfiguracion = crearConfiguracion;
        configuracion = new Configuracion();
    }

    public boolean isCrearConfiguracion() {
        return crearConfiguracion;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }
}
