package es.jklabs.gui.configuracion;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.configuracion.panel.MysqlConfigPanel;
import es.jklabs.gui.configuracion.panel.ServersConfigPanel;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.Mensajes;

import javax.swing.*;
import java.awt.*;

public class ConfiguracionUI extends JDialog {

    private static final long serialVersionUID = -5947416101394804262L;
    private MainUI padre;
    private Configuracion configuracion;
    private MainUI ventanaPrincipal;

    public ConfiguracionUI(MainUI mainUI, Configuracion configuracion) {
        super(mainUI, Mensajes.getMensaje("configuracion"), true);
        this.padre = mainUI;
        this.configuracion = configuracion;
        cargarPantalla();
    }

    private void cargarPantalla() {
        this.setLayout(new BorderLayout());
        JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        jTabbedPane.add(Mensajes.getMensaje("mysql"), new MysqlConfigPanel(this, configuracion));
        jTabbedPane.add(Mensajes.getMensaje("servidores"), new ServersConfigPanel(padre, this, configuracion));
        this.add(jTabbedPane, BorderLayout.CENTER);
        this.pack();
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    public MainUI getVentanaPrincipal() {
        return ventanaPrincipal;
    }

    public void setVentanaPrincipal(MainUI ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
    }

}
