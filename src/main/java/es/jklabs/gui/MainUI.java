package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.Logger;

import javax.swing.*;
import java.text.ParseException;

public class MainUI extends JFrame {

    private static final Logger LOG = Logger.getLogger();
    private static final long serialVersionUID = 4591514658240490883L;

    private Configuracion configuracion;

    private MainUI() {
        super("BeyondDataBaseTransfer");
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JMenuBar menu = new JMenuBar();
        JMenu jmArchivo = new JMenu("Archivo");
        JMenuItem jmiConfiguracion = new JMenuItem("Configuración");
        jmiConfiguracion.addActionListener(al -> abrirConfiguracion());
        JMenuItem jmiExportar = new JMenuItem("Exportar Servidores");
        jmiExportar.addActionListener(al -> exportarServidores());
        JMenuItem jmiImportar = new JMenuItem("Importar Servidores");
        jmiImportar.addActionListener(al -> importarServidores());
        jmArchivo.add(jmiConfiguracion);
        jmArchivo.add(jmiExportar);
        jmArchivo.add(jmiImportar);
        JMenu jmAyuda = new JMenu("Ayuda");
        JMenuItem jmiAcercaDe = new JMenuItem("Acerca de...");
        jmAyuda.add(jmiAcercaDe);
        menu.add(jmArchivo);
        menu.add(jmAyuda);
        super.setJMenuBar(menu);
        super.pack();
    }

    private void importarServidores() {
        //ToDO
    }

    private void exportarServidores() {
        //ToDo
    }

    private void abrirConfiguracion() {
        try {
            ConfiguracionUI configuracionUI = new ConfiguracionUI(this, configuracion);
            configuracionUI.setVisible(true);
        } catch (ParseException e) {
            LOG.error("Cargar dialogo configuración", e);
        }
    }

    public MainUI(Configuracion configuracion) {
        this();
        this.configuracion = configuracion;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }
}
