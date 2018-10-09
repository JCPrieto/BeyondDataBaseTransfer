package es.jklabs.gui.utilidades;

import es.jklabs.gui.MainUI;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import java.awt.*;

public class Growls {

    private Growls() {

    }

    public static void mostrarError(MainUI parent, String cuerpo, Exception e) {
        mostrarError(parent, null, cuerpo, e);
    }

    public static void mostrarError(MainUI parent, String titulo, String cuerpo, Exception e) {
        parent.getTrayIcon().displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getError
                (cuerpo), TrayIcon.MessageType.ERROR);
        Logger.error(cuerpo, e);
    }

    public static void mostrarError(MainUI parent, String titulo, String cuerpo) {
        mostrarError(parent, titulo, cuerpo, false);
    }

    public static void mostrarError(MainUI parent, String titulo, String cuerpo, boolean log) {
        parent.getTrayIcon().displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getError
                (cuerpo), TrayIcon.MessageType.ERROR);
        if (log) {
            Logger.error(cuerpo);
        }
    }

    public static void mostrarInfo(MainUI parent, String titulo, String cuerpo) {
        parent.getTrayIcon().displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getMensaje
                (cuerpo), TrayIcon.MessageType.INFO);
    }

    public static void mostrarAviso(MainUI parent, String titulo, String cuerpo) {
        parent.getTrayIcon().displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, Mensajes.getError
                (cuerpo), TrayIcon.MessageType.WARNING);
    }

}
