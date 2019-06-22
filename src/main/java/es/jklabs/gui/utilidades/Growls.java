package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Growls {

    private static final String NOTIFY_SEND = "notify-send";
    private static TrayIcon trayIcon;

    private Growls() {

    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), TrayIcon.MessageType.ERROR, "--icon=dialog-error");
        Logger.error(cuerpo, e);
    }

    private static void mostrarGrowl(String titulo, String cuerpo, TrayIcon.MessageType type, String icon) {
        if (trayIcon != null) {
            trayIcon.displayMessage(titulo != null ? Mensajes.getMensaje(titulo) : null, cuerpo, type);
        } else {
            try {
                Runtime.getRuntime().exec(new String[]{NOTIFY_SEND,
                        titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP,
                        cuerpo,
                        icon});
            } catch (IOException e2) {
                Logger.error(e2);
            }
        }
    }

    public static void mostrarError(String titulo, String cuerpo) {
        mostrarError(titulo, cuerpo, false);
    }

    public static void mostrarError(String titulo, String cuerpo, boolean log) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), TrayIcon.MessageType.ERROR, "--icon=dialog-error");
        if (log) {
            Logger.error(cuerpo);
        }
    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        mostrarGrowl(titulo, Mensajes.getMensaje(cuerpo), TrayIcon.MessageType.INFO, "--icon=dialog-information");
    }

    public static void mostrarAviso(String titulo, String cuerpo) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), TrayIcon.MessageType.WARNING, "--icon=dialog-warning");
    }

    public static void init() {
        trayIcon = null;
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(Growls.class.getClassLoader().getResource
                    ("img/icons/newpct.png"))).getImage(), Constantes.NOMBRE_APP);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                Logger.error("establecer.icono.systray", e);
            }
        }
    }
}
