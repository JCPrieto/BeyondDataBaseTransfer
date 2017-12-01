package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.gui.utilidades.filtro.JSonFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class MainUI extends JFrame {

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
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new JSonFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retorno = fc.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            UtilidadesConfiguracion.loadServidores(configuracion, file);
        }
    }

    private void exportarServidores() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new JSonFilter());
        fc.setAcceptAllFileFilterUsed(false);
        int retorno = fc.showSaveDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!Objects.equals(FilenameUtils.getExtension(file.getName()), "json")) {
                file = new File(file.toString() + ".json");
            }
            UtilidadesConfiguracion.guardarServidores(configuracion.getServerConfig(), file);
        }
    }

    private void abrirConfiguracion() {
        ConfiguracionUI configuracionUI = new ConfiguracionUI(this, configuracion);
        configuracionUI.setVisible(true);
    }

    public MainUI(Configuracion configuracion) {
        this();
        this.configuracion = configuracion;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }
}
