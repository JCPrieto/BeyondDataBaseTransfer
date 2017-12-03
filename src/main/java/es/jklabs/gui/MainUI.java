package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.gui.utilidades.filtro.JSonFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import org.apache.commons.io.FilenameUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainUI extends JFrame {

    private static final long serialVersionUID = 4591514658240490883L;

    private Configuracion configuracion;
    private JTree arbolOrigen;
    private DefaultMutableTreeNode raizArbolOrigen;
    private JTree arbolDestino;
    private JTextField txEsquema;
    private JProgressBar progressBar;
    private List<String> listaEsquemas;
    private DefaultMutableTreeNode raizArbolDestino;

    private MainUI() {
        super("BeyondDataBaseTransfer");
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cargarMenu();
        super.pack();
    }

    public MainUI(Configuracion configuracion) {
        this();
        this.configuracion = configuracion;
        cargarPantallaPrincipal();
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

    private void cargarMenu() {
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
    }

    private void cargarPantallaPrincipal() {
        super.setLayout(new BorderLayout(10, 10));
        JPanel panelCentral = new JPanel(new GridLayout(1, 2));
        panelCentral.add(cargarPanelArbolOrigen());
        panelCentral.add(cargarPanelArbolDestino());
        super.add(panelCentral, BorderLayout.CENTER);
        super.add(cargarFormulario(), BorderLayout.SOUTH);
    }

    private JPanel cargarFormulario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel lbEsquema = new JLabel("Esquema");
        txEsquema = new JTextField();
        txEsquema.setColumns(10);
        txEsquema.setFocusTraversalKeysEnabled(false);
        listaEsquemas = new ArrayList<>();
        AutoCompleteDecorator.decorate(txEsquema, listaEsquemas, false);
        JButton btnAceptar = new JButton("Copiar");
        btnAceptar.addActionListener(al -> copiarEsquema());
        progressBar = new JProgressBar(0, 6);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        panel.add(lbEsquema, BorderLayout.WEST);
        panel.add(txEsquema, BorderLayout.CENTER);
        panel.add(btnAceptar, BorderLayout.EAST);
        panel.add(progressBar, BorderLayout.SOUTH);
        return panel;
    }

    private void copiarEsquema() {
        DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        if (origen != null && origen.getUserObject() instanceof Servidor && destino != null && destino.getUserObject
                () instanceof Servidor && !txEsquema.getText().trim().isEmpty()) {
            int count = 1;
            //ToDo Conectar con el servidor de origen
            progressBar.setValue(count++);
            SwingUtilities.updateComponentTreeUI(progressBar);
            //ToDo Crear dump
            progressBar.setValue(count++);
            SwingUtilities.updateComponentTreeUI(progressBar);
            //ToDo Descargar dump
            progressBar.setValue(count++);
            SwingUtilities.updateComponentTreeUI(progressBar);
            //ToDo Subir dump al destino
            progressBar.setValue(count++);
            SwingUtilities.updateComponentTreeUI(progressBar);
            //ToDo Conectar con el serivdor de destino
            progressBar.setValue(count++);
            SwingUtilities.updateComponentTreeUI(progressBar);
            //ToDo restaurar dump
            progressBar.setValue(count);
            SwingUtilities.updateComponentTreeUI(progressBar);
        }
    }

    private JScrollPane cargarPanelArbolDestino() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbolDestino = new DefaultMutableTreeNode(carpetaRaiz);
        arbolDestino = new JTree(raizArbolDestino);
        arbolDestino.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        JScrollPane scroll = new JScrollPane(arbolDestino);
        scroll.setBorder(BorderFactory.createTitledBorder("Destino"));
        return scroll;
    }

    private JScrollPane cargarPanelArbolOrigen() {
        cargarArbolOrigen();
        arbolOrigen = new JTree(raizArbolOrigen);
        arbolOrigen.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolOrigen.addTreeSelectionListener(tsl -> selecionarOrigen());
        UtilidadesJTree.expandAllNodes(arbolOrigen, 0, arbolOrigen.getRowCount());
        JScrollPane scroll = new JScrollPane(arbolOrigen);
        scroll.setBorder(BorderFactory.createTitledBorder("Origen"));
        return scroll;
    }

    private void selecionarOrigen() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        raizArbolDestino.removeAllChildren();
        if (node != null && node.getUserObject() instanceof Servidor) {
            Servidor servidor = (Servidor) node.getUserObject();
            cargarArbolDestino(servidor);
            cargarEsquemas(servidor);
        }
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbolDestino);
    }

    private void cargarEsquemas(Servidor servidor) {
        listaEsquemas.clear();
        if (servidor.getServidor() != null) {
            servidor.getServidor().forEach(s -> listaEsquemas.add(s.getNombre()));
        }
    }

    private void cargarArbolDestino(Servidor servidorDescartable) {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        addElementos(carpetaRaiz, raizArbolDestino, servidorDescartable);
    }

    private void addElementos(Carpeta carpeta, DefaultMutableTreeNode padre, Servidor servidorDescartable) {
        carpeta.getCarpetas().forEach(c -> addCarpeta(padre, c, servidorDescartable));
        carpeta.getServidores().stream().filter(s -> !Objects.equals(s, servidorDescartable)).forEach(s -> padre.add
                (new
                        DefaultMutableTreeNode(s)));
    }

    private void addCarpeta(DefaultMutableTreeNode padre, Carpeta carpeta, Servidor servidorDescartable) {
        DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(carpeta);
        padre.add(hijo);
        addElementos(carpeta, hijo, servidorDescartable);
    }

    private void cargarArbolOrigen() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbolOrigen = new DefaultMutableTreeNode(carpetaRaiz);
        addElementos(carpetaRaiz, raizArbolOrigen);
    }

    private void addElementos(Carpeta carpetaRaiz, DefaultMutableTreeNode padre) {
        carpetaRaiz.getCarpetas().forEach(c -> addCarpeta(padre, c));
        carpetaRaiz.getServidores().forEach(s -> padre.add(new DefaultMutableTreeNode(s)));
    }

    private void addCarpeta(DefaultMutableTreeNode padre, Carpeta carpeta) {
        DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(carpeta);
        padre.add(hijo);
        addElementos(carpeta, hijo);
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }
}
