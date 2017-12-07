package es.jklabs.gui.utilidades;

import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Objects;

public class ArbolRendered implements TreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof Carpeta) {
            if (expanded) {
                return new JLabel(
                        node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                        .getClassLoader().getResource
                                ("img/icons/folder-blue-open.png"))), JLabel.CENTER);
            } else {
                return new JLabel(
                        node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                        .getClassLoader().getResource
                                ("img/icons/folder-blue.png"))), JLabel.CENTER);
            }
        } else if (node.getUserObject() instanceof Servidor) {
            return new JLabel(
                    node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource
                            ("img/icons/application-x-sqlite2.png"))), JLabel.CENTER);
        }
        return null;
    }
}
