package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.gui.dialogos.AcercaDe;
import es.jklabs.gui.utilidades.ArbolRendered;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.gui.utilidades.filtro.JSonFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.utilidades.CopySchema;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import es.jklabs.utilidades.UtilidadesFirebase;
import org.apache.commons.io.FilenameUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainUI extends JFrame {

    private static final long serialVersionUID = 4591514658240490883L;

    private static final Logger LOG = Logger.getLogger();

    private Configuracion configuracion;
    private JTree arbolOrigen;
    private DefaultMutableTreeNode raizArbolOrigen;
    private JTree arbolDestino;
    private JTextField txEsquema;
    private JProgressBar progressBar;
    private List<String> listaEsquemas;
    private DefaultMutableTreeNode raizArbolDestino;
    private JButton btnAceptar;
    private transient TrayIcon trayIcon;
    private JMenu jmArchivo;
    private JMenu jmAyuda;

    private MainUI() {
        super("BeyondDataBaseTransfer");
        super.setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                ("img/icons/database.png"))).getImage());
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cargarMenu();
        cargarNotificaciones();
        super.pack();
    }

    private void cargarNotificaciones() {
        SystemTray tray = SystemTray.getSystemTray();
        //Alternative (if the icon is on the classpath):
        trayIcon = new TrayIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                ("img/icons/database.png"))).getImage(), "Tray Demo");
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("BeyondDataBaseTransfer");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            LOG.error("Establecer icono del systray", e);
        }
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
            recargarArboles();
        }
    }

    private void recargarArboles() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbolOrigen.removeAllChildren();
        addElementos(carpetaRaiz, raizArbolOrigen);
        arbolOrigen.setModel(new DefaultTreeModel(raizArbolOrigen, false));
        UtilidadesJTree.expandAllNodes(arbolOrigen, 0, arbolOrigen.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbolOrigen);
        raizArbolDestino.removeAllChildren();
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbolDestino);
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
        recargarArboles();
    }

    private void cargarMenu() {
        JMenuBar menu = new JMenuBar();
        jmArchivo = new JMenu("Archivo");
        jmArchivo.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiConfiguracion = new JMenuItem("Configuración", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/settings.png"))));
        jmiConfiguracion.addActionListener(al -> abrirConfiguracion());
        JMenuItem jmiExportar = new JMenuItem("Exportar Servidores", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/download.png"))));
        jmiExportar.addActionListener(al -> exportarServidores());
        JMenuItem jmiImportar = new JMenuItem("Importar Servidores", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/upload.png"))));
        jmiImportar.addActionListener(al -> importarServidores());
        jmArchivo.add(jmiConfiguracion);
        jmArchivo.add(jmiExportar);
        jmArchivo.add(jmiImportar);
        jmAyuda = new JMenu("Ayuda");
        jmAyuda.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiAcercaDe = new JMenuItem("Acerca de...", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("img/icons/info.png"))));
        jmiAcercaDe.addActionListener(al -> mostrarAcercaDe());
        jmAyuda.add(jmiAcercaDe);
        menu.add(jmArchivo);
        menu.add(jmAyuda);
        if (UtilidadesFirebase.existeNuevaVersion()) {
            menu.add(Box.createHorizontalGlue());
            JMenuItem jmActualizacion = new JMenuItem("Existe una nueva versión", new ImageIcon(Objects.requireNonNull
                    (getClass().getClassLoader().getResource("img/icons/update.png"))));
            jmActualizacion.addActionListener(al -> UtilidadesFirebase.descargaNuevaVersion(this));
            menu.add(jmActualizacion);
        }
        super.setJMenuBar(menu);
    }

    private void mostrarAcercaDe() {
        AcercaDe acercaDe = new AcercaDe(this);
        acercaDe.setVisible(true);
    }

    private void cargarPantallaPrincipal() {
        super.setLayout(new BorderLayout(10, 10));
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCentral.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelCentral.add(cargarPanelArbolOrigen());
        panelCentral.add(cargarPanelArbolDestino());
        super.add(panelCentral, BorderLayout.CENTER);
        super.add(cargarFormulario(), BorderLayout.SOUTH);
    }

    private JPanel cargarFormulario() {
        JPanel panelFormulario = new JPanel(new BorderLayout(10, 10));
        panelFormulario.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lbEsquema = new JLabel("Esquema");
        txEsquema = new JTextField();
        txEsquema.setColumns(10);
        txEsquema.setFocusTraversalKeysEnabled(false);
        listaEsquemas = new ArrayList<>();
        AutoCompleteDecorator.decorate(txEsquema, listaEsquemas, false);
        btnAceptar = new JButton("Copiar");
        btnAceptar.addActionListener(al -> copiarEsquema());
        progressBar = new JProgressBar(0, 9);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        panelFormulario.add(lbEsquema, BorderLayout.WEST);
        panelFormulario.add(txEsquema, BorderLayout.CENTER);
        panelFormulario.add(btnAceptar, BorderLayout.EAST);
        panelFormulario.add(progressBar, BorderLayout.SOUTH);
        return panelFormulario;
    }

    private void copiarEsquema() {
        DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolDestino.getLastSelectedPathComponent();
        if (origen != null && origen.getUserObject() instanceof Servidor && destino != null && destino.getUserObject
                () instanceof Servidor && !txEsquema.getText().trim().isEmpty()) {
            bloquearPantalla();
            CopySchema task = new CopySchema(this, (Servidor) origen.getUserObject(),
                    (Servidor) destino.getUserObject(), txEsquema.getText().trim());
            task.addPropertyChangeListener(pcl -> changeListener(pcl.getPropertyName(), pcl.getNewValue()));
            task.execute();
            UtilidadesConfiguracion.addEsquema(configuracion, (Servidor) origen.getUserObject(),
                    (Servidor) destino.getUserObject(), txEsquema.getText().trim());
            listaEsquemas.add(txEsquema.getText().trim());
        }
    }

    private void bloquearPantalla() {
        jmArchivo.setEnabled(false);
        jmAyuda.setEnabled(false);
        arbolOrigen.setEnabled(false);
        arbolDestino.setEnabled(false);
        txEsquema.setEnabled(false);
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        txEsquema.setCursor(waitCursor);
        btnAceptar.setEnabled(false);
        this.setCursor(waitCursor);
    }

    private void changeListener(String propertyName, Object newValue) {
        if (Objects.equals(propertyName, "progress")) {
            progressBar.setValue((Integer) newValue);
        }
    }

    private JScrollPane cargarPanelArbolDestino() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbolDestino = new DefaultMutableTreeNode(carpetaRaiz);
        arbolDestino = new JTree(raizArbolDestino);
        arbolDestino.setCellRenderer(new ArbolRendered());
        arbolDestino.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        JScrollPane scroll = new JScrollPane(arbolDestino);
        scroll.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 5, 0, 10), "Destino"));
        scroll.setPreferredSize(new Dimension(200, 300));
        return scroll;
    }

    private JScrollPane cargarPanelArbolOrigen() {
        cargarArbolOrigen();
        arbolOrigen = new JTree(raizArbolOrigen);
        arbolOrigen.setCellRenderer(new ArbolRendered());
        arbolOrigen.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolOrigen.addTreeSelectionListener(tsl -> selecionarOrigen());
        UtilidadesJTree.expandAllNodes(arbolOrigen, 0, arbolOrigen.getRowCount());
        JScrollPane scroll = new JScrollPane(arbolOrigen);
        scroll.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 10, 0, 5), "Origen"));
        scroll.setPreferredSize(new Dimension(200, 300));
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
        arbolDestino.setModel(new DefaultTreeModel(raizArbolDestino, false));
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbolDestino);
    }

    private void cargarEsquemas(Servidor servidor) {
        listaEsquemas.clear();
        if (servidor.getEsquemas() != null) {
            servidor.getEsquemas().forEach(s -> listaEsquemas.add(s.getNombre()));
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

    public JButton getBtnAceptar() {
        return btnAceptar;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void desbloquearPantalla() {
        jmArchivo.setEnabled(true);
        jmAyuda.setEnabled(true);
        arbolOrigen.setEnabled(true);
        arbolDestino.setEnabled(true);
        txEsquema.setEnabled(true);
        txEsquema.setCursor(null);
        getBtnAceptar().setEnabled(true);
        setCursor(null); //turn off the wait cursor
    }
}
