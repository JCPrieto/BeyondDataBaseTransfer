package es.jklabs.gui.configuracion.panel;

import es.jklabs.gui.configuracion.ConfiguracionUI;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.utilidades.UtilidadesConfiguracion;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class MysqlConfigPanel extends JPanel {

    private static final long serialVersionUID = 3500969681893444957L;
    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private final Configuracion configuracion;
    private final ConfiguracionUI dialogo;
    private JTextField txPath;

    public MysqlConfigPanel(ConfiguracionUI dialogo, Configuracion configuracion) {
        super();
        this.dialogo = dialogo;
        this.configuracion = configuracion;
        cargarPanel();
    }

    private void cargarPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel jLabel = new JLabel(mensajes.getString("ruta.instalacion.mysql"));
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridwidth = 2;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(jLabel, c);
        if (configuracion.getMysqlCliente() != null) {
            txPath = new JTextField(configuracion.getMysqlCliente().getPath());
        } else {
            txPath = new JTextField();
        }
        txPath.setColumns(30);
        txPath.setEditable(false);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        add(txPath, c);
        JButton jButton = new JButton(mensajes.getString("seleccionar"));
        jButton.addActionListener(l -> seleccionarRuta());
        c.gridx = 1;
        c.gridy = 1;
        add(jButton, c);
    }

    private void seleccionarRuta() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showSaveDialog(dialogo);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            if (configuracion.getMysqlCliente() == null) {
                configuracion.setMysqlCliente(new MysqlCliente());
            }
            configuracion.getMysqlCliente().setPath(directorio.getAbsolutePath());
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
            txPath.setText(directorio.getAbsolutePath());
        }
    }
}
