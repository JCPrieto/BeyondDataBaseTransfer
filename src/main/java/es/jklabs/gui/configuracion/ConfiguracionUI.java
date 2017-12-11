package es.jklabs.gui.configuracion;

import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.ArbolRendered;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.gui.utilidades.filtro.PuertoDocumentoFilter;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import es.jklabs.json.utilidades.enumeradores.MetodoLoggin;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import es.jklabs.utilidades.UtilidadesEncryptacion;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ConfiguracionUI extends JDialog {

    private static final String SELECCIONAR_ARCHIVO = "Seleccionar archivo...";
    private static final long serialVersionUID = -5947416101394804262L;
    private Configuracion configuracion;
    private JTree arbol;
    private JButton btnAceptar;
    private JTextField txNombre;
    private JTextField txIp;
    private JTextField txPuerto;
    private JComboBox<MetodoLoggin> cbLoginMethod;
    private JTextField txSshUser;
    private JTextField txBbddUser;
    private JPasswordField txBbddPasword;
    private JPasswordField txSshPasword;
    private JLabel lbRsaUrlFile;
    private JLabel lbSshPassword;
    private JLabel lbRsaFile;
    private JPanel panelFormularioServidor;
    private JButton btnRsaFileChooser;
    private File rsaKeyFIle;
    private JPanel panelBotoneraArbol;
    private DefaultMutableTreeNode raizArbol;

    public ConfiguracionUI(MainUI mainUI, Configuracion configuracion) {
        super(mainUI, "Servidores", true);
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
        panel.add(cargarFormulario(), BorderLayout.CENTER);
        panel.add(cargarBotoneraFormulario(), BorderLayout.SOUTH);
        setEnableFormularioServidor(false);
        return panel;
    }

    private void setEnableFormularioServidor(boolean enable) {
        btnAceptar.setEnabled(enable);
        Arrays.stream(panelFormularioServidor.getComponents()).forEach(c -> c.setEnabled(enable));
    }

    private JPanel cargarBotoneraFormulario() {
        JPanel panel = new JPanel();
        btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(al -> guardarServidor());
        panel.add(btnAceptar);
        return panel;
    }

    private void guardarServidor() {
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
            servidor.setUsuario(txSshUser.getText());
            MetodoLoggin metodo = (MetodoLoggin) cbLoginMethod.getSelectedItem();
            servidor.setMetodoLoggin(metodo);
            if (metodo == MetodoLoggin.CONTRASENA) {
                servidor.setPassword(UtilidadesEncryptacion.encrypt(String.valueOf(txSshPasword.getPassword())));
                servidor.setKeyUrl(null);
            } else if (metodo == MetodoLoggin.KEY_FILE) {
                servidor.setKeyUrl(rsaKeyFIle.getAbsolutePath());
                servidor.setPassword(null);
            }
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

    private void setEnableArbol(boolean enable) {
        arbol.setEnabled(enable);
        Arrays.stream(panelBotoneraArbol.getComponents()).forEach(c -> c.setEnabled(enable));
    }

    private JPanel cargarFormulario() {
        panelFormularioServidor = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel lbNombre = new JLabel("Nombre");
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
        JLabel lbIp = new JLabel("Host");
        c.gridx = 0;
        c.gridy = 1;
        panelFormularioServidor.add(lbIp, c);
        txIp = new JTextField();
        txIp.setColumns(10);
        c.gridx = 1;
        c.gridy = 1;
        panelFormularioServidor.add(txIp, c);
        JLabel lbPuerto = new JLabel("Puerto");
        c.gridx = 0;
        c.gridy = 2;
        panelFormularioServidor.add(lbPuerto, c);
        txPuerto = new JTextField();
        ((PlainDocument) txPuerto.getDocument()).setDocumentFilter(new PuertoDocumentoFilter());
        txPuerto.setColumns(3);
        c.gridx = 1;
        c.gridy = 2;
        panelFormularioServidor.add(txPuerto, c);
        JLabel lbSshUser = new JLabel("Usuario SSH");
        c.gridx = 0;
        c.gridy = 3;
        panelFormularioServidor.add(lbSshUser, c);
        txSshUser = new JTextField();
        txSshUser.setColumns(10);
        c.gridx = 1;
        c.gridy = 3;
        panelFormularioServidor.add(txSshUser, c);
        JLabel lbLoginMethod = new JLabel("Metodo de autenticación");
        c.gridx = 0;
        c.gridy = 4;
        panelFormularioServidor.add(lbLoginMethod, c);
        cbLoginMethod = new JComboBox<>(MetodoLoggin.values());
        cbLoginMethod.addActionListener(ae -> seleccionarMetodoAutenticacion());
        c.gridx = 1;
        c.gridy = 4;
        panelFormularioServidor.add(cbLoginMethod, c);

        lbSshPassword = new JLabel("Contraseña SSH");
        c.gridx = 0;
        c.gridy = 5;
        panelFormularioServidor.add(lbSshPassword, c);
        txSshPasword = new JPasswordField();
        txSshPasword.setColumns(10);
        c.gridx = 1;
        c.gridy = 5;
        panelFormularioServidor.add(txSshPasword, c);

        lbRsaFile = new JLabel("Archivo de clave RSA");
        c.gridx = 0;
        c.gridy = 5;
        lbRsaFile.setVisible(false);
        panelFormularioServidor.add(lbRsaFile, c);
        lbRsaUrlFile = new JLabel(SELECCIONAR_ARCHIVO);
        lbRsaUrlFile.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                ("img/icons/key-file.png"))));
        c.gridx = 1;
        c.gridy = 5;
        lbRsaUrlFile.setVisible(false);
        panelFormularioServidor.add(lbRsaUrlFile, c);
        btnRsaFileChooser = new JButton("Seleccionar");
        btnRsaFileChooser.addActionListener(ae -> seleccionarRsaKeyFile());
        c.gridx = 1;
        c.gridy = 6;
        btnRsaFileChooser.setVisible(false);
        panelFormularioServidor.add(btnRsaFileChooser, c);

        JLabel lbBbddUser = new JLabel("Usuario BBDD");
        c.gridx = 0;
        c.gridy = 7;
        c.anchor = GridBagConstraints.LINE_START;
        panelFormularioServidor.add(lbBbddUser, c);
        txBbddUser = new JTextField();
        txBbddUser.setColumns(10);
        c.gridx = 1;
        c.gridy = 7;
        panelFormularioServidor.add(txBbddUser, c);
        JLabel lbBbddPassword = new JLabel("Contraseña BBDD");
        c.gridx = 0;
        c.gridy = 8;
        panelFormularioServidor.add(lbBbddPassword, c);
        txBbddPasword = new JPasswordField();
        txBbddPasword.setColumns(10);
        c.gridx = 1;
        c.gridy = 8;
        panelFormularioServidor.add(txBbddPasword, c);
        return panelFormularioServidor;
    }

    private void seleccionarRsaKeyFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retorno = fc.showOpenDialog(this);
        if (Objects.equals(retorno, JFileChooser.APPROVE_OPTION)) {
            rsaKeyFIle = fc.getSelectedFile();
            lbRsaUrlFile.setText(rsaKeyFIle.getName());
        } else {
            lbRsaUrlFile.setText(SELECCIONAR_ARCHIVO);
        }
    }

    private JPanel cargarPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(cargarPanelArbol(), BorderLayout.CENTER);
        panel.add(cargarBotoneraArbol(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cargarBotoneraArbol() {
        panelBotoneraArbol = new JPanel(new GridLayout(2, 2));
        JButton btnAddFolder = new JButton("Añadir carpeta");
        btnAddFolder.addActionListener(al -> addCarpetaToArbol());
        JButton btnAddServer = new JButton("Añadir servidor");
        btnAddServer.addActionListener(al -> addServidorToArbol());
        JButton btnEdit = new JButton("Editar");
        btnEdit.addActionListener(al -> editarNodoArbol());
        JButton btnDelete = new JButton("Eliminar");
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
                DialogoCarpeta dialogoCarpeta = new DialogoCarpeta(this, carpeta);
                dialogoCarpeta.setVisible(true);
            } else if (parentNode.getUserObject() instanceof Servidor) {
                Servidor servidor = (Servidor) parentNode.getUserObject();
                txNombre.setText(servidor.getNombre());
                txIp.setText(servidor.getIp());
                txPuerto.setText(String.valueOf(servidor.getPuerto()));
                txSshUser.setText(servidor.getUsuario());
                cbLoginMethod.setSelectedItem(servidor.getMetodoLoggin());
                if (servidor.getMetodoLoggin() == MetodoLoggin.CONTRASENA) {
                    txSshPasword.setText(UtilidadesEncryptacion.decrypt(servidor.getPassword()));
                } else if (servidor.getMetodoLoggin() == MetodoLoggin.KEY_FILE) {
                    lbRsaUrlFile.setText(servidor.getKeyUrl());
                    rsaKeyFIle = new File(servidor.getKeyUrl());
                }
                txBbddUser.setText(servidor.getServidorBBDD().getUsuario());
                txBbddPasword.setText(UtilidadesEncryptacion.decrypt(servidor.getServidorBBDD().getPassword()));
                setEnableArbol(false);
                setEnableFormularioServidor(true);
            }
            UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
            SwingUtilities.updateComponentTreeUI(arbol);
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
        }
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
            DialogoCarpeta dialogoCarpeta = new DialogoCarpeta(this, nuevaCarpeta);
            dialogoCarpeta.setVisible(true);
            ((Carpeta) parentNode.getUserObject()).getCarpetas().add(nuevaCarpeta);
            addCarpeta(parentNode, nuevaCarpeta);
            UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
            SwingUtilities.updateComponentTreeUI(arbol);
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
        }
    }

    private JScrollPane cargarPanelArbol() {
        cargarArbol();
        arbol = new JTree(raizArbol);
        arbol.setCellRenderer(new ArbolRendered());
        arbol.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
        return new JScrollPane(arbol);
    }

    private void cargarArbol() {
        Carpeta carpetaRaiz = configuracion.getServerConfig().getRaiz();
        raizArbol = new DefaultMutableTreeNode(carpetaRaiz);
        addElementos(carpetaRaiz, raizArbol);
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

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    private void seleccionarMetodoAutenticacion() {
        MetodoLoggin i = (MetodoLoggin) cbLoginMethod.getSelectedItem();
        if (i == MetodoLoggin.CONTRASENA) {
            lbSshPassword.setVisible(true);
            txSshPasword.setVisible(true);
            lbRsaFile.setVisible(false);
            lbRsaUrlFile.setText(SELECCIONAR_ARCHIVO);
            lbRsaUrlFile.setVisible(false);
            btnRsaFileChooser.setVisible(false);
        } else if (i == MetodoLoggin.KEY_FILE) {
            lbSshPassword.setVisible(false);
            txSshPasword.setVisible(false);
            lbRsaFile.setVisible(true);
            lbRsaUrlFile.setVisible(true);
            btnRsaFileChooser.setVisible(true);
        }
        SwingUtilities.updateComponentTreeUI(panelFormularioServidor);
    }

}
