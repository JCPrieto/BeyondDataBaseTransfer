package es.jklabs.gui;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.gui.dialogos.AcercaDe;
import es.jklabs.gui.utilidades.ArbolRendered;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.gui.utilidades.IconUtils;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.gui.utilidades.filtro.JSonFilter;
import es.jklabs.gui.utilidades.filtro.SqlFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.utilidades.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 4591514658240490883L;

    private static final String COPIAR_ESQUEMA = "copiar.esquema";

    private Configuracion configuracion;
    private JTree arbolOrigen;
    private DefaultMutableTreeNode raizArbolOrigen;
    private JTree arbolDestino;
    private JComboBox<String> cbEsquema;
    private JProgressBar progressBar;
    private DefaultMutableTreeNode raizArbolDestino;
    private JButton btnAceptar;
    private JMenu jmArchivo;
    private JMenu jmAyuda;
    private JCheckBox cbLimpiar;
    private JButton btnCrearBackup;
    private JButton btnRestaurarBackup;
    private int schemaLoadSequence;

    private MainUI() {
        super(Constantes.NOMBRE_APP);
        Image appIcon = IconUtils.loadImage("database.png");
        if (appIcon != null) {
            super.setIconImage(appIcon);
            setTaskbarIcon(appIcon);
        }
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cargarMenu();
        super.pack();
    }

    private void setTaskbarIcon(Image appIcon) {
        if (!Taskbar.isTaskbarSupported()) {
            return;
        }
        Taskbar taskbar = Taskbar.getTaskbar();
        if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
            return;
        }
        try {
            taskbar.setIconImage(appIcon);
        } catch (UnsupportedOperationException | SecurityException e) {
            Logger.error("establecer.icono.aplicacion", e);
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
        limpiarSelectorEsquemas();
        raizArbolDestino.removeAllChildren();
        addElementosDestino(carpetaRaiz, raizArbolDestino);
        arbolDestino.setModel(new DefaultTreeModel(raizArbolDestino, false));
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
                file = new File(file + ".json");
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
        jmArchivo = new JMenu(Mensajes.getMensaje("archivo"));
        jmArchivo.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiConfiguracion = new JMenuItem(Mensajes.getMensaje("configuracion"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/settings.png"))));
        jmiConfiguracion.addActionListener(al -> abrirConfiguracion());
        JMenuItem jmiExportar = new JMenuItem(Mensajes.getMensaje("exportar.servidores"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/download.png"))));
        jmiExportar.addActionListener(al -> exportarServidores());
        JMenuItem jmiImportar = new JMenuItem(Mensajes.getMensaje("importar.servidores"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/upload.png"))));
        jmiImportar.addActionListener(al -> importarServidores());
        jmArchivo.add(jmiConfiguracion);
        jmArchivo.add(jmiExportar);
        jmArchivo.add(jmiImportar);
        jmAyuda = new JMenu("Ayuda");
        jmAyuda.setMargin(new Insets(5, 5, 5, 5));
        JMenuItem jmiAcercaDe = new JMenuItem(Mensajes.getMensaje("acerca.de"), new ImageIcon(Objects
                .requireNonNull(getClass().getClassLoader().getResource("img/icons/info.png"))));
        jmiAcercaDe.addActionListener(al -> mostrarAcercaDe());
        jmAyuda.add(jmiAcercaDe);
        menu.add(jmArchivo);
        menu.add(jmAyuda);
        super.setJMenuBar(menu);
        consultarNuevaVersion();
    }

    private void consultarNuevaVersion() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return UtilidadesGithubReleases.existeNuevaVersion();
                } catch (InterruptedException e) {
                    Logger.error("consultar.nueva.version", e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Logger.error("consultar.nueva.version", e);
                }
                return false;
            }

            @Override
            protected void done() {
                try {
                    if (Boolean.TRUE.equals(get())) {
                        mostrarNuevaVersionDisponible();
                    }
                } catch (InterruptedException e) {
                    Logger.error("consultar.nueva.version", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Logger.error("consultar.nueva.version", e);
                }
            }
        };
        worker.execute();
    }

    private void mostrarNuevaVersionDisponible() {
        JMenuBar menu = getJMenuBar();
        menu.add(Box.createHorizontalGlue());
        JMenuItem jmActualizacion = new JMenuItem(Mensajes.getMensaje("existe.nueva.version"), new ImageIcon
                (Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/update.png"))));
        jmActualizacion.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        jmActualizacion.setHorizontalTextPosition(SwingConstants.RIGHT);
        jmActualizacion.addActionListener(al -> descargarNuevaVersion());
        menu.add(jmActualizacion);
        menu.revalidate();
        menu.repaint();
    }

    private void descargarNuevaVersion() {
        try {
            UtilidadesGithubReleases.descargaNuevaVersion(this);
        } catch (InterruptedException e) {
            Growls.mostrarError("descargar.nueva.version", e);
            Thread.currentThread().interrupt();
        }
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
        JPanel panelFormulario = new JPanel(new BorderLayout(0, 5));
        panelFormulario.setBorder(new EmptyBorder(10, 10, 10, 10));
        cbLimpiar = new JCheckBox(Mensajes.getMensaje("limpiar.esquema"));
        progressBar = new JProgressBar(0, 3);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        panelFormulario.add(getFormularioEsquema(), BorderLayout.NORTH);
        panelFormulario.add(cbLimpiar, BorderLayout.CENTER);
        panelFormulario.add(progressBar, BorderLayout.SOUTH);
        return panelFormulario;
    }

    private JPanel getFormularioEsquema() {
        JPanel panelFormulario = new JPanel(new BorderLayout(10, 10));
        JLabel lbEsquema = new JLabel(Mensajes.getMensaje("esquema"));
        cbEsquema = new JComboBox<>();
        cbEsquema.setPrototypeDisplayValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        AutoCompleteDecorator.decorate(cbEsquema);
        JPanel panelAcciones = new JPanel(new GridLayout(1, 3, 5, 0));
        btnAceptar = new JButton(Mensajes.getMensaje("copiar"));
        btnAceptar.addActionListener(al -> copiarEsquema());
        btnCrearBackup = new JButton(Mensajes.getMensaje("crear.backup"));
        btnCrearBackup.addActionListener(al -> crearBackup());
        btnRestaurarBackup = new JButton(Mensajes.getMensaje("restaurar.backup"));
        btnRestaurarBackup.addActionListener(al -> restaurarBackup());
        panelAcciones.add(btnAceptar);
        panelAcciones.add(btnCrearBackup);
        panelAcciones.add(btnRestaurarBackup);
        panelFormulario.add(lbEsquema, BorderLayout.WEST);
        panelFormulario.add(cbEsquema, BorderLayout.CENTER);
        panelFormulario.add(panelAcciones, BorderLayout.EAST);
        return panelFormulario;
    }

    private void copiarEsquema() {
        if (validaCopiado()) {
            DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
            DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolDestino.getLastSelectedPathComponent();
            if (origen != null && origen.getUserObject() instanceof Servidor && destino != null && destino.getUserObject
                    () instanceof Servidor && StringUtils.isNotBlank(getEsquemaSeleccionado())) {
                String esquema = getEsquemaSeleccionado();
                bloquearPantalla();
                prepararProgressBar(3);
                CopySchema task = new CopySchema(this, configuracion.getMysqlCliente(), (Servidor) origen
                        .getUserObject(), (Servidor) destino.getUserObject(), esquema,
                        cbLimpiar.isSelected());
                task.addPropertyChangeListener(pcl -> changeListener(pcl.getPropertyName(), pcl.getNewValue()));
                task.execute();
                UtilidadesConfiguracion.addEsquema(configuracion, (Servidor) origen.getUserObject(),
                        (Servidor) destino.getUserObject(), esquema);
            }
        }
    }

    private void crearBackup() {
        if (validaCrearBackup()) {
            DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
            String esquema = getEsquemaSeleccionado();
            File backup = seleccionarArchivoBackupParaGuardar(esquema);
            if (backup != null) {
                bloquearPantalla();
                prepararProgressBar(2);
                BackupSchema task = new BackupSchema(this, configuracion.getMysqlCliente(), (Servidor) origen
                        .getUserObject(), esquema, backup);
                task.addPropertyChangeListener(pcl -> changeListener(pcl.getPropertyName(), pcl.getNewValue()));
                task.execute();
            }
        }
    }

    private void restaurarBackup() {
        if (validaRestaurarBackup()) {
            DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolDestino.getLastSelectedPathComponent();
            File backup = seleccionarArchivoBackupParaAbrir();
            if (backup != null) {
                bloquearPantalla();
                prepararProgressBar(2);
                RestoreBackup task = new RestoreBackup(this, configuracion.getMysqlCliente(), (Servidor) destino
                        .getUserObject(), backup);
                task.addPropertyChangeListener(pcl -> changeListener(pcl.getPropertyName(), pcl.getNewValue()));
                task.execute();
            }
        }
    }

    private File seleccionarArchivoBackupParaGuardar(String esquema) {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new SqlFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new File(esquema + ".sql"));
        int retorno = fc.showSaveDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!Objects.equals(FilenameUtils.getExtension(file.getName()), "sql")) {
                file = new File(file + ".sql");
            }
            return file;
        }
        return null;
    }

    private File seleccionarArchivoBackupParaAbrir() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new SqlFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retorno = fc.showOpenDialog(this);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    private void prepararProgressBar(int maximo) {
        progressBar.setMaximum(maximo);
        progressBar.setValue(0);
    }

    private String getEsquemaSeleccionado() {
        Object selectedItem = cbEsquema.getSelectedItem();
        return selectedItem != null ? selectedItem.toString().trim() : "";
    }

    private boolean validaCopiado() {
        boolean valido = true;
        DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        if (origen == null || !(origen.getUserObject() instanceof Servidor)) {
            Growls.mostrarAviso(COPIAR_ESQUEMA, "bbdd.origen.vacio");
            valido = false;
        }
        DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolDestino.getLastSelectedPathComponent();
        if (destino == null || !(destino.getUserObject() instanceof Servidor)) {
            Growls.mostrarAviso(COPIAR_ESQUEMA, "bbdd.destino.vacio");
            valido = false;
        }
        if (StringUtils.isBlank(getEsquemaSeleccionado())) {
            Growls.mostrarAviso(COPIAR_ESQUEMA, "nombre.esquema.vacio");
            valido = false;
        }
        return validarClienteMysql(COPIAR_ESQUEMA, valido, true, true);
    }

    private boolean validaCrearBackup() {
        boolean valido = true;
        DefaultMutableTreeNode origen = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        if (origen == null || !(origen.getUserObject() instanceof Servidor)) {
            Growls.mostrarAviso("crear.backup", "bbdd.origen.vacio");
            valido = false;
        }
        if (StringUtils.isBlank(getEsquemaSeleccionado())) {
            Growls.mostrarAviso("crear.backup", "nombre.esquema.vacio");
            valido = false;
        }
        return validarClienteMysql("crear.backup", valido, false, true);
    }

    private boolean validaRestaurarBackup() {
        boolean valido = true;
        DefaultMutableTreeNode destino = (DefaultMutableTreeNode) arbolDestino.getLastSelectedPathComponent();
        if (destino == null || !(destino.getUserObject() instanceof Servidor)) {
            Growls.mostrarAviso("restaurar.backup", "bbdd.destino.vacio");
            valido = false;
        }
        return validarClienteMysql("restaurar.backup", valido, true, false);
    }

    private boolean validarClienteMysql(String titulo, boolean valido, boolean requiereMysql, boolean requiereMysqlDump) {
        if (configuracion.getMysqlCliente() == null ||
                StringUtils.isEmpty(configuracion.getMysqlCliente().getPath())) {
            Growls.mostrarAviso(titulo, "ruta.instalacion.mysql.no.configurada");
            valido = false;
        } else {
            String comandoMysql;
            String comandoMysqlDump;
            if (UtilidadesSistema.isWindows()) {
                comandoMysql = Constantes.MYSQL_EXE;
                comandoMysqlDump = Constantes.MYSQLDUMP_EXE;
            } else {
                comandoMysql = Constantes.MYSQL;
                comandoMysqlDump = Constantes.MYSQLDUMP;
            }
            File mysql = new File(configuracion.getMysqlCliente().getPath() + UtilidadesFichero.SEPARADOR +
                    comandoMysql);
            File mysqlDump = new File(configuracion.getMysqlCliente().getPath() + UtilidadesFichero.SEPARADOR +
                    comandoMysqlDump);
            if (requiereMysql && !mysql.exists()) {
                Growls.mostrarAviso(titulo, "orden.mysql.no.encontrado");
                valido = false;
            }
            if (requiereMysqlDump && !mysqlDump.exists()) {
                Growls.mostrarAviso(titulo, "orden.mysqldump.no.encontrado");
                valido = false;
            }
        }
        return valido;
    }

    private void bloquearPantalla() {
        setPantallaBloqueada(true);
    }

    private void setPantallaBloqueada(boolean bloqueada) {
        Cursor cursor = bloqueada ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
        jmArchivo.setEnabled(!bloqueada);
        jmAyuda.setEnabled(!bloqueada);
        arbolOrigen.setEnabled(!bloqueada);
        arbolDestino.setEnabled(!bloqueada);
        cbEsquema.setEnabled(!bloqueada);
        cbLimpiar.setEnabled(!bloqueada);
        cbEsquema.setCursor(cursor);
        btnAceptar.setEnabled(!bloqueada);
        btnCrearBackup.setEnabled(!bloqueada);
        btnRestaurarBackup.setEnabled(!bloqueada);
        this.setCursor(cursor);
        getGlassPane().setCursor(cursor);
        getGlassPane().setVisible(bloqueada);
    }

    private void changeListener(String propertyName, Object newValue) {
        if (Objects.equals(propertyName, "progress")) {
            progressBar.setValue((Integer) newValue);
        }
    }

    private JScrollPane cargarPanelArbolDestino() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbolDestino = new DefaultMutableTreeNode(carpetaRaiz);
        addElementosDestino(carpetaRaiz, raizArbolDestino);
        arbolDestino = new JTree(raizArbolDestino);
        arbolDestino.setCellRenderer(new ArbolRendered());
        arbolDestino.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        JScrollPane scroll = new JScrollPane(arbolDestino);
        scroll.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 5, 0, 10), Mensajes.getMensaje
                ("destino")));
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
        scroll.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 10, 0, 5), Mensajes.getMensaje
                ("origen")));
        scroll.setPreferredSize(new Dimension(200, 300));
        return scroll;
    }

    private void selecionarOrigen() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbolOrigen.getLastSelectedPathComponent();
        raizArbolDestino.removeAllChildren();
        limpiarSelectorEsquemas();
        if (node != null && node.getUserObject() instanceof Servidor servidor) {
            cargarArbolDestino(servidor);
            cargarEsquemas(servidor);
        }
        arbolDestino.setModel(new DefaultTreeModel(raizArbolDestino, false));
        UtilidadesJTree.expandAllNodes(arbolDestino, 0, arbolDestino.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbolDestino);
    }

    private void cargarEsquemas(Servidor servidor) {
        if (!puedeConsultarEsquemas(servidor)) {
            return;
        }
        int loadSequence = ++schemaLoadSequence;
        bloquearPantalla();
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws IOException, InterruptedException {
                return consultarEsquemas(servidor);
            }

            @Override
            protected void done() {
                if (loadSequence != schemaLoadSequence) {
                    return;
                }
                try {
                    cargarSelectorEsquemas(get());
                } catch (InterruptedException e) {
                    Logger.error("consultar.esquemas", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Logger.error("consultar.esquemas", e);
                    limpiarSelectorEsquemas();
                } finally {
                    desbloquearPantalla();
                }
            }
        };
        worker.execute();
    }

    private boolean puedeConsultarEsquemas(Servidor servidor) {
        return configuracion.getMysqlCliente() != null
                && StringUtils.isNotBlank(configuracion.getMysqlCliente().getPath())
                && servidor.getServidorBBDD() != null
                && StringUtils.isNotBlank(servidor.getServidorBBDD().getUsuario())
                && StringUtils.isNotBlank(servidor.getServidorBBDD().getPassword());
    }

    private List<String> consultarEsquemas(Servidor servidor) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(getMysqlArgsConsultarEsquemas(servidor)).start();
        Thread errorReader = descartarErroresAsync(p);
        List<String> esquemas;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            esquemas = reader.lines()
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .toList();
        }
        int exitCode = p.waitFor();
        errorReader.join();
        if (exitCode != 0) {
            throw new IOException("Consulta de esquemas finalizada con codigo " + exitCode);
        }
        return esquemas;
    }

    private Thread descartarErroresAsync(Process p) {
        Thread thread = new Thread(() -> {
            try (InputStream errorStream = p.getErrorStream()) {
                errorStream.transferTo(OutputStream.nullOutputStream());
            } catch (IOException e) {
                Logger.error("consultar.esquemas", e);
            }
        }, "schema-list-error-reader");
        thread.start();
        return thread;
    }

    private List<String> getMysqlArgsConsultarEsquemas(Servidor servidor) {
        return List.of(
                getMysqlPath(),
                "-h", servidor.getIp(),
                "-P", String.valueOf(servidor.getPuerto()),
                "-u" + servidor.getServidorBBDD().getUsuario(),
                "-p" + UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()),
                "--batch",
                "--skip-column-names",
                "-e", "SHOW DATABASES"
        );
    }

    private String getMysqlPath() {
        String comandoMysql = UtilidadesSistema.isWindows() ? Constantes.MYSQL_EXE : Constantes.MYSQL;
        return configuracion.getMysqlCliente().getPath() + UtilidadesFichero.SEPARADOR + comandoMysql;
    }

    private void cargarSelectorEsquemas(List<String> esquemas) {
        cbEsquema.removeAllItems();
        esquemas.forEach(cbEsquema::addItem);
        cbEsquema.setSelectedItem(null);
    }

    private void limpiarSelectorEsquemas() {
        schemaLoadSequence++;
        if (cbEsquema != null) {
            cbEsquema.removeAllItems();
            cbEsquema.setSelectedItem(null);
        }
    }

    private void cargarArbolDestino(Servidor servidorDescartable) {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        addElementos(carpetaRaiz, raizArbolDestino, servidorDescartable);
    }

    private void addElementos(Carpeta carpeta, DefaultMutableTreeNode padre, Servidor servidorDescartable) {
        carpeta.getCarpetas().forEach(c -> addCarpeta(padre, c, servidorDescartable));
        carpeta.getServidores().stream()
                .filter(s -> !s.isSoloOrigen())
                .filter(s -> !Objects.equals(s, servidorDescartable))
                .forEach(s -> padre.add(new DefaultMutableTreeNode(s)));
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

    private void addElementosDestino(Carpeta carpetaRaiz, DefaultMutableTreeNode padre) {
        carpetaRaiz.getCarpetas().forEach(c -> addCarpetaDestino(padre, c));
        carpetaRaiz.getServidores().stream()
                .filter(s -> !s.isSoloOrigen())
                .forEach(s -> padre.add(new DefaultMutableTreeNode(s)));
    }

    private void addCarpetaDestino(DefaultMutableTreeNode padre, Carpeta carpeta) {
        DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(carpeta);
        padre.add(hijo);
        addElementosDestino(carpeta, hijo);
    }

    private void addCarpeta(DefaultMutableTreeNode padre, Carpeta carpeta) {
        DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(carpeta);
        padre.add(hijo);
        addElementos(carpeta, hijo);
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void desbloquearPantalla() {
        setPantallaBloqueada(false);
    }
}
