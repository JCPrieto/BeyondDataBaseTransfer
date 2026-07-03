package es.jklabs.gui.utilidades;

import com.sshtools.twoslices.*;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Growls {

    private static DesktopNotifier notifier = new TwoSlicesDesktopNotifier();
    private static URL notificationIcon;

    private Growls() {

    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), NotificationType.ERROR);
        Logger.error(cuerpo, e);
    }

    private static void mostrarGrowl(String titulo, String cuerpo, NotificationType type) {
        String tituloMensaje = titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP;
        try {
            notifier.show(tituloMensaje, cuerpo, type, notificationIcon);
        } catch (RuntimeException | LinkageError e) {
            Logger.error("mostrar.notificacion", e);
            mostrarDialogo(tituloMensaje, cuerpo, type);
        }
    }

    private static void mostrarDialogo(String titulo, String cuerpo, NotificationType type) {
        if (GraphicsEnvironment.isHeadless()) {
            Logger.info(titulo + ": " + cuerpo);
            return;
        }
        Runnable showDialog = () -> JOptionPane.showMessageDialog(null, cuerpo, titulo, type.optionPaneMessageType());
        if (SwingUtilities.isEventDispatchThread()) {
            showDialog.run();
        } else {
            SwingUtilities.invokeLater(showDialog);
        }
    }

    public static void mostrarError(String titulo, String cuerpo) {
        mostrarError(titulo, cuerpo, false);
    }

    public static void mostrarError(String titulo, String cuerpo, boolean log) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), NotificationType.ERROR);
        if (log) {
            Logger.error(cuerpo);
        }
    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        mostrarGrowl(titulo, Mensajes.getMensaje(cuerpo), NotificationType.INFO);
    }

    public static void mostrarAviso(String titulo, String cuerpo) {
        mostrarGrowl(titulo, Mensajes.getError(cuerpo), NotificationType.WARNING);
    }

    public static void init() {
        notificationIcon = Growls.class.getResource("/img/icons/database.png");
        ToasterSettings settings = new ToasterSettings()
                .setAppName(Constantes.NOMBRE_APP)
                .setTimeout(10);
        if (notificationIcon != null) {
            settings.setDefaultImage(notificationIcon);
        }
        ToasterFactory.setSettings(settings);
    }

    enum NotificationType {
        INFO(ToastType.INFO, JOptionPane.INFORMATION_MESSAGE),
        WARNING(ToastType.WARNING, JOptionPane.WARNING_MESSAGE),
        ERROR(ToastType.ERROR, JOptionPane.ERROR_MESSAGE);

        private final ToastType toastType;
        private final int optionPaneMessageType;

        NotificationType(ToastType toastType, int optionPaneMessageType) {
            this.toastType = toastType;
            this.optionPaneMessageType = optionPaneMessageType;
        }

        private ToastType toastType() {
            return toastType;
        }

        private int optionPaneMessageType() {
            return optionPaneMessageType;
        }
    }

    interface DesktopNotifier {
        void show(String title, String body, NotificationType type, URL icon);
    }

    private static class TwoSlicesDesktopNotifier implements DesktopNotifier {

        @Override
        public void show(String title, String body, NotificationType type, URL icon) {
            ToastBuilder builder = Toast.builder()
                    .type(type.toastType())
                    .title(title)
                    .content(body);
            if (icon != null) {
                builder.icon(icon);
            }
            builder.toast();
        }
    }
}
