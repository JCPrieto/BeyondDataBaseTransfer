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
        String tituloMensaje = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        if (trayIcon != null) {
            trayIcon.displayMessage(tituloMensaje, cuerpo, type);
            return;
        }
        if (mostrarNotifySend(tituloMensaje, cuerpo, icon)) {
            return;
        }
        mostrarDialogo(tituloMensaje, cuerpo, type);
    }

    private static boolean mostrarNotifySend(String titulo, String cuerpo, String icon) {
        if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
            return false;
        }
        try {
            new ProcessBuilder(NOTIFY_SEND, titulo, cuerpo, icon).start();
            return true;
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }

    private static void mostrarDialogo(String titulo, String cuerpo, TrayIcon.MessageType type) {
        if (GraphicsEnvironment.isHeadless()) {
            Logger.info(titulo + ": " + cuerpo);
            return;
        }
        Runnable showDialog = () -> JOptionPane.showMessageDialog(null, cuerpo, titulo, getMessageType(type));
        if (SwingUtilities.isEventDispatchThread()) {
            showDialog.run();
        } else {
            SwingUtilities.invokeLater(showDialog);
        }
    }

    private static int getMessageType(TrayIcon.MessageType type) {
        if (type == TrayIcon.MessageType.ERROR) {
            return JOptionPane.ERROR_MESSAGE;
        }
        if (type == TrayIcon.MessageType.WARNING) {
            return JOptionPane.WARNING_MESSAGE;
        }
        return JOptionPane.INFORMATION_MESSAGE;
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
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(Growls.class.getClassLoader().getResource
                    ("img/icons/database.png"))).getImage(), Constantes.NOMBRE_APP);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                trayIcon = null;
                Logger.error("establecer.icono.systray", e);
            }
        }
    }
}
