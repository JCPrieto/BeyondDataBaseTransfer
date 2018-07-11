package es.jklabs.gui.configuracion;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.ArbolRendered;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.gui.utilidades.filtro.PuertoDocumentoFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import es.jklabs.utilidades.UtilidadesEncryptacion;
import es.jklabs.utilidades.UtilidadesString;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ConfiguracionUI extends JDialog {

    private static final String ANADIR_CARPETA = "anadir.carpeta";
    private static final String ANADIR_SERVIDOR = "anadir.servidor";
    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private static final long serialVersionUID = -5947416101394804262L;
    private MainUI padre;
    private Configuracion configuracion;
    private JTree arbol;
    private JButton btnAceptar;
    private JTextField txNombre;
    private JTextField txIp;
    private JTextField txPuerto;
    private JTextField txBbddUser;
    private JPasswordField txBbddPasword;
    private JPanel panelFormularioServidor;
    private JPanel panelBotoneraArbol;
    private DefaultMutableTreeNode raizArbol;
    private JButton btnCancelar;

    public ConfiguracionUI(MainUI mainUI, Configuracion configuracion) {
        super(mainUI, mensajes.getString("servidores"), true);
        this.padre = mainUI;
        this.configuracion = configuracion;
        cargarPantalla();
    }

    private void cargarPantalla() {
        this.setLayout(new BorderLayout());
        this.add(cargarPanelCentral(), BorderLayout.CENTER);
        this.add(cargarPanelDerecho(), BorderLayout.EAST);
        this.pack();
    }

    private JPanel cargarPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(cargarFormulario(), BorderLayout.CENTER);
        panel.add(cargarBotoneraFormulario(), BorderLayout.SOUTH);
        setEnableFormularioServidor(false);
        return panel;
    }

    private void setEnableFormularioServidor(boolean enable) {
        btnAceptar.setEnabled(enable);
        btnCancelar.setEnabled(enable);
        Arrays.stream(panelFormularioServidor.getComponents()).forEach(c -> c.setEnabled(enable));
    }

    private JPanel cargarBotoneraFormulario() {
        JPanel panel = new JPanel();
        btnAceptar = new JButton(mensajes.getString("aceptar"));
        btnAceptar.addActionListener(al -> guardarServidor());
        btnCancelar = new JButton(mensajes.getString("cancelar"));
        btnCancelar.addActionListener(al -> cancelarCreacionServidor());
        panel.add(btnAceptar);
        panel.add(btnCancelar);
        return panel;
    }

    private void cancelarCreacionServidor() {
        setEnableArbol(true);
        setEnableFormularioServidor(false);
        SwingUtilities.updateComponentTreeUI(arbol);
    }

    private void guardarServidor() {
        if (validaFormularioServidor()) {
            guardarServidor2();
        }
    }

    private void guardarServidor2() {
        DefaultMutableTreeNode parentNode;
        TreePath parentPath = arbol.getSelectionPath();
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
        } else {
            parentNode = raizArbol;
        }
        Servidor servidor = null;
        if (parentNode.getUserObject() instanceof Carpeta) {
            servidor = new Servidor(UtilidadesConfiguracion.getIdServidor(configuracion));
            ((Carpeta) parentNode.getUserObject()).getServidores().add(servidor);
            parentNode.add(new DefaultMutableTreeNode(servidor));
        } else if (parentNode.getUserObject() instanceof Servidor) {
            servidor = (Servidor) parentNode.getUserObject();
        }
        if (servidor != null) {
            servidor.setNombre(txNombre.getText());
            servidor.setIp(txIp.getText());
            servidor.setPuerto(Integer.valueOf(txPuerto.getText()));
            ServidorBBDD servidorBBDD;
            if (servidor.getServidorBBDD() != null) {
                servidorBBDD = servidor.getServidorBBDD();
            } else {
                servidorBBDD = new ServidorBBDD();
                servidor.setServidorBBDD(servidorBBDD);
            }
            servidorBBDD.setUsuario(txBbddUser.getText());
            servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt(String.valueOf(txBbddPasword.getPassword())));
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
            setEnableArbol(true);
            setEnableFormularioServidor(false);
            UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
            SwingUtilities.updateComponentTreeUI(arbol);
        }
    }

    private boolean validaFormularioServidor() {
        boolean valido = true;
        if (UtilidadesString.isEmpty(txNombre)) {
            valido = false;
            Growls.mostrarAviso(padre, ANADIR_SERVIDOR, "nombre.servidor.vacio");
        }
        if (UtilidadesString.isEmpty(txIp)) {
            valido = false;
            Growls.mostrarAviso(padre, ANADIR_SERVIDOR, "ip.servidor.vacio");
        }
        if (UtilidadesString.isEmpty(txPuerto)) {
            valido = false;
            Growls.mostrarAviso(padre, ANADIR_SERVIDOR, "puerto.servidor.vacio");
        }
        if (UtilidadesString.isEmpty(txBbddUser)) {
            valido = false;
            Growls.mostrarAviso(padre, ANADIR_SERVIDOR, "usuario.bbdd.servidor.vacio");
        }
        if (UtilidadesString.isEmpty(txBbddPasword)) {
            valido = false;
            Growls.mostrarAviso(padre, ANADIR_SERVIDOR, "password.bbdd.servidor.vacio");
        }
        return valido;
    }

    private void setEnableArbol(boolean enable) {
        arbol.setEnabled(enable);
        Arrays.stream(panelBotoneraArbol.getComponents()).forEach(c -> c.setEnabled(enable));
    }

    private JPanel cargarFormulario() {
        panelFormularioServidor = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel lbNombre = new JLabel(mensajes.getString("nombre"));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.LINE_START;
        panelFormularioServidor.add(lbNombre, c);
        txNombre = new JTextField();
        txNombre.setColumns(10);
        c.gridx = 1;
        c.gridy = 0;
        panelFormularioServidor.add(txNombre, c);
        JLabel lbIp = new JLabel(mensajes.getString("host"));
        c.gridx = 0;
        c.gridy = 1;
        panelFormularioServidor.add(lbIp, c);
        txIp = new JTextField();
        txIp.setColumns(10);
        c.gridx = 1;
        c.gridy = 1;
        panelFormularioServidor.add(txIp, c);
        JLabel lbPuerto = new JLabel(mensajes.getString("puerto"));
        c.gridx = 0;
        c.gridy = 2;
        panelFormularioServidor.add(lbPuerto, c);
        txPuerto = new JTextField();
        ((PlainDocument) txPuerto.getDocument()).setDocumentFilter(new PuertoDocumentoFilter());
        txPuerto.setColumns(3);
        c.gridx = 1;
        c.gridy = 2;
        panelFormularioServidor.add(txPuerto, c);
        JLabel lbBbddUser = new JLabel(mensajes.getString("usuario.bbdd"));
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_START;
        panelFormularioServidor.add(lbBbddUser, c);
        txBbddUser = new JTextField();
        txBbddUser.setColumns(10);
        c.gridx = 1;
        c.gridy = 6;
        panelFormularioServidor.add(txBbddUser, c);
        JLabel lbBbddPassword = new JLabel(mensajes.getString("contrasena.bbdd"));
        c.gridx = 0;
        c.gridy = 7;
        panelFormularioServidor.add(lbBbddPassword, c);
        txBbddPasword = new JPasswordField();
        txBbddPasword.setColumns(10);
        c.gridx = 1;
        c.gridy = 7;
        panelFormularioServidor.add(txBbddPasword, c);
        return panelFormularioServidor;
    }

    private JPanel cargarPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(cargarPanelArbol(), BorderLayout.CENTER);
        panel.add(cargarBotoneraArbol(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cargarBotoneraArbol() {
        panelBotoneraArbol = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotoneraArbol.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton btnAddFolder = new JButton(mensajes.getString(ANADIR_CARPETA));
        btnAddFolder.addActionListener(al -> addCarpetaToArbol());
        JButton btnAddServer = new JButton(mensajes.getString(ANADIR_SERVIDOR));
        btnAddServer.addActionListener(al -> addServidorToArbol());
        JButton btnEdit = new JButton(mensajes.getString("editar"));
        btnEdit.addActionListener(al -> editarNodoArbol());
        JButton btnDelete = new JButton(mensajes.getString("eliminar"));
        btnDelete.addActionListener(al -> eliminarNodoArbol());
        panelBotoneraArbol.add(btnAddFolder);
        panelBotoneraArbol.add(btnAddServer);
        panelBotoneraArbol.add(btnEdit);
        panelBotoneraArbol.add(btnDelete);
        return panelBotoneraArbol;
    }

    private void eliminarNodoArbol() {
        TreePath parentPath = arbol.getSelectionPath();
        DefaultMutableTreeNode parentNode;
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
            if (parentNode.getUserObject() instanceof Carpeta) {
                Carpeta carpeta = (Carpeta) parentNode.getUserObject();
                eliminarCarpeta(carpeta);
            } else if (parentNode.getUserObject() instanceof Servidor) {
                Servidor servidor = (Servidor) parentNode.getUserObject();
                eliminarServidor(servidor);
            }
            parentNode.removeFromParent();
        }
        UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
        SwingUtilities.updateComponentTreeUI(arbol);
        UtilidadesConfiguracion.guardarConfiguracion(configuracion);
    }

    private void eliminarServidor(Servidor servidor) {
        Iterator<Servidor> it = configuracion.getServerConfig().getRaiz().getServidores().iterator();
        if (eliminarServidor(servidor, it)) {
            eliminarServidor(configuracion.getServerConfig().getRaiz().getCarpetas(), servidor);
        }
    }

    private boolean eliminarServidor(Servidor servidor, Iterator<Servidor> it) {
        boolean enc = false;
        while (it.hasNext() && !enc) {
            Servidor s = it.next();
            if (Objects.equals(servidor, s)) {
                it.remove();
                enc = true;
            }
        }
        return !enc;
    }

    private boolean eliminarServidor(List<Carpeta> carpetas, Servidor servidor) {
        boolean enc = false;
        for (Carpeta c : carpetas) {
            if (eliminarServidor(servidor, c.getServidores().iterator())) {
                enc = eliminarServidor(c.getCarpetas(), servidor);
            }
            if (enc) {
                break;
            }
        }
        return enc;
    }

    private void eliminarCarpeta(Carpeta carpeta) {
        if (!carpeta.isSistema()) {
            Iterator<Carpeta> it = configuracion.getServerConfig().getRaiz().getCarpetas().iterator();
            eliminarCarpeta(it, carpeta);
        }
    }

    private boolean eliminarCarpeta(Iterator<Carpeta> it, Carpeta carpeta) {
        boolean enc = false;
        while (it.hasNext() && !enc) {
            Carpeta c = it.next();
            if (Objects.equals(carpeta, c)) {
                it.remove();
                enc = true;
            } else {
                enc = eliminarCarpeta(c.getCarpetas().iterator(), carpeta);
            }
        }
        return enc;
    }

    private void editarNodoArbol() {
        TreePath parentPath = arbol.getSelectionPath();
        DefaultMutableTreeNode parentNode;
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
            if (parentNode.getUserObject() instanceof Carpeta) {
                Carpeta carpeta = (Carpeta) parentNode.getUserObject();
                DialogoCarpeta dialogoCarpeta = new DialogoCarpeta(this, carpeta, false);
                dialogoCarpeta.setVisible(true);
            } else if (parentNode.getUserObject() instanceof Servidor) {
                cargarDatosFormularioServidor(parentNode);
                setEnableArbol(false);
                setEnableFormularioServidor(true);
                UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
                SwingUtilities.updateComponentTreeUI(arbol);
                UtilidadesConfiguracion.guardarConfiguracion(configuracion);
            }
        }
    }

    private void cargarDatosFormularioServidor(DefaultMutableTreeNode parentNode) {
        Servidor servidor = (Servidor) parentNode.getUserObject();
        txNombre.setText(servidor.getNombre());
        txIp.setText(servidor.getIp());
        txPuerto.setText(String.valueOf(servidor.getPuerto()));
        txBbddUser.setText(servidor.getServidorBBDD().getUsuario());
        txBbddPasword.setText(UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()));
        pack();
    }

    private void addServidorToArbol() {
        TreePath parentPath = arbol.getSelectionPath();
        DefaultMutableTreeNode parentNode;
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
        } else {
            parentNode = raizArbol;
        }
        if (parentNode.getUserObject() instanceof Carpeta) {
            setEnableArbol(false);
            setEnableFormularioServidor(true);
            SwingUtilities.updateComponentTreeUI(arbol);
        } else {
            Growls.mostrarAviso(padre, ANADIR_CARPETA, "anadir.servidor.en.servidor");
        }
    }

    private void addCarpetaToArbol() {
        TreePath parentPath = arbol.getSelectionPath();
        DefaultMutableTreeNode parentNode;
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
        } else {
            parentNode = raizArbol;
        }
        if (parentNode.getUserObject() instanceof Carpeta) {
            Carpeta nuevaCarpeta = new Carpeta(UtilidadesConfiguracion.getIdCarpeta(configuracion));
            DialogoCarpeta dialogoCarpeta = new DialogoCarpeta(this, nuevaCarpeta, true);
            dialogoCarpeta.setVisible(true);
        } else {
            Growls.mostrarAviso(padre, ANADIR_CARPETA, "anadir.carpeta.en.servidor");
        }
    }

    private JScrollPane cargarPanelArbol() {
        cargarArbol();
        arbol = new JTree(raizArbol);
        arbol.setCellRenderer(new ArbolRendered());
        arbol.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbol.addTreeSelectionListener(tsl -> seleccionarNodo());
        UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
        JScrollPane jScrollPane = new JScrollPane(arbol);
        jScrollPane.setPreferredSize(new Dimension(200, 300));
        return jScrollPane;
    }

    private void seleccionarNodo() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
        if (node != null && node.getUserObject() instanceof Servidor) {
            cargarDatosFormularioServidor(node);
        } else {
            limpiarFormularioServidor();
        }
    }

    private void limpiarFormularioServidor() {
        txNombre.setText(null);
        txIp.setText(null);
        txPuerto.setText(null);
        txBbddUser.setText(null);
        txBbddPasword.setText(null);
    }

    private void cargarArbol() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbol = new DefaultMutableTreeNode(carpetaRaiz);
        UtilidadesJTree.addElementos(carpetaRaiz, raizArbol);
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    JTree getArbol() {
        return arbol;
    }

    DefaultMutableTreeNode getRaizArbol() {
        return raizArbol;
    }

    MainUI getPadre() {
        return padre;
    }

}
