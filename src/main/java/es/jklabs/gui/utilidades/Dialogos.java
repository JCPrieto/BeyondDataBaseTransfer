package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Dialogos {

    private static final Logger LOG = Logger.getLogger();
    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private static ResourceBundle errores = ResourceBundle.getBundle("i18n/errores", Locale.getDefault());

    private Dialogos() {

    }

    public static void mostrarAviso(Component component, String titulo, String cuerpo) {
        JOptionPane.showMessageDialog(component, errores.getString(cuerpo), mensajes.getString(titulo), JOptionPane
                .WARNING_MESSAGE);
    }

    public static void mostrarError(Component component, String titulo, String cuerpo, Exception e) {
        JOptionPane.showMessageDialog(component, errores.getString(cuerpo), mensajes.getString(titulo), JOptionPane
                .ERROR_MESSAGE);
        LOG.error(cuerpo, e);
    }
}
