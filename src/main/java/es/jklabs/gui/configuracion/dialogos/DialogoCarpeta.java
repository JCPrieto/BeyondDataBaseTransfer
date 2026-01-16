package es.jklabs.gui.configuracion.dialogos;

import es.jklabs.gui.configuracion.panel.ServersConfigPanel;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.gui.utilidades.UtilidadesJTree;
import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesConfiguracion;
import es.jklabs.utilidades.UtilidadesString;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;

public class DialogoCarpeta extends JDialog {

    private static final long serialVersionUID = 7372193257865030732L;
    private final Carpeta carpeta;
    private final JTree arbol;
    private final Configuracion configuracion;
    private final DefaultMutableTreeNode parentNode;
    private final boolean nuevo;
    private JTextField txNombre;

    public DialogoCarpeta(ServersConfigPanel panel, Carpeta carpeta, boolean nuevo) {
        super(panel.getDialogo(), Mensajes.getMensaje("carpeta"), true);
        this.arbol = panel.getArbol();
        this.configuracion = panel.getConfiguracion();
        TreePath parentPath = arbol.getSelectionPath();
        if (parentPath != null) {
            parentNode = (DefaultMutableTreeNode)
                    (parentPath.getLastPathComponent());
        } else {
            parentNode = panel.getRaizArbol();
        }
        this.carpeta = carpeta;
        this.nuevo = nuevo;
        cargarPantalla();
    }

    private void cargarPantalla() {
        JPanel formulario = new JPanel(new BorderLayout(10, 10));
        formulario.setBorder(new EmptyBorder(10, 10, 10, 10));
        formulario.add(new JLabel(Mensajes.getMensaje("nombre")), BorderLayout.WEST);
        txNombre = new JTextField(carpeta.getNombre());
        txNombre.setColumns(10);
        formulario.add(txNombre, BorderLayout.CENTER);
        JButton btnAceptar = new JButton(Mensajes.getMensaje("aceptar"));
        btnAceptar.addActionListener(al -> guardarNombre());
        formulario.add(btnAceptar, BorderLayout.SOUTH);
        this.add(formulario);
        this.pack();
    }

    private void guardarNombre() {
        if (UtilidadesString.notEmpty(txNombre)) {
            carpeta.setNombre(txNombre.getText());
            if (nuevo) {
                ((Carpeta) parentNode.getUserObject()).getCarpetas().add(carpeta);
                UtilidadesJTree.addCarpeta(parentNode, carpeta);
            }
            UtilidadesJTree.expandAllNodes(arbol, 0, arbol.getRowCount());
            SwingUtilities.updateComponentTreeUI(arbol);
            UtilidadesConfiguracion.guardarConfiguracion(configuracion);
            super.dispose();
        } else {
            Growls.mostrarAviso("anadir.carpeta", "nombre.carpeta.vacio");
        }
    }
}
