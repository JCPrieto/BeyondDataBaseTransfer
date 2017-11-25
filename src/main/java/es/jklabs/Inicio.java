package es.jklabs;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesConfiguracion;

import javax.swing.*;

public class Inicio {

    private static final Logger LOG = Logger.getLogger();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Configuracion configuracion = UtilidadesConfiguracion.loadConfig();
            if (configuracion == null) {
                ConfiguracionUI configuracionUI = new ConfiguracionUI(true);
                configuracionUI.setVisible(true);
            } else {
                MainUI mainUI = new MainUI(configuracion);
                mainUI.setVisible(true);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            LOG.error("Cargar el LookAndFeel del S.O", e);
        }
    }
}
