package es.jklabs;

import es.jklabs.utilidades.Logger;

import javax.swing.*;

public class Inicio {

    private static final Logger LOG = Logger.getLogger();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            LOG.error("Cargar el LookAndFeel del S.O", e);
        }
    }
}
