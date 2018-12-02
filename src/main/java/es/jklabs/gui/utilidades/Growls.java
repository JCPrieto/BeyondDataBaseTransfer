package es.jklabs.gui.utilidades;

import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;

public class Growls {

    private Growls() {

    }

    public static void mostrarError(String cuerpo, Exception e) {
        mostrarError(null, cuerpo, e);
    }

    public static void mostrarError(String titulo, String cuerpo, Exception e) {
        Platform.runLater(() -> getGrowl(titulo, Mensajes.getError(cuerpo))
                .showError());
        Logger.error(cuerpo, e);
    }

    private static Notifications getGrowl(String titulo, String cuerpo) {
        return Notifications.create()
                .darkStyle()
                .title(titulo != null ? Mensajes.getMensaje(titulo) : Constantes.NOMBRE_APP)
                .text(cuerpo);
    }

    public static void mostrarError(String titulo, String cuerpo) {
        mostrarError(titulo, cuerpo, false);
    }

    public static void mostrarError(String titulo, String cuerpo, boolean log) {
        Platform.runLater(() -> getGrowl(titulo, Mensajes.getError(cuerpo))
                .showError());
        if (log) {
            Logger.error(cuerpo);
        }
    }

    public static void mostrarInfo(String titulo, String cuerpo) {
        Platform.runLater(() -> getGrowl(titulo, Mensajes.getMensaje(cuerpo))
                .showInformation());
    }

    public static void mostrarAviso(String titulo, String cuerpo) {
        Platform.runLater(() -> getGrowl(titulo, Mensajes.getError(cuerpo))
                .showWarning());
    }

}
