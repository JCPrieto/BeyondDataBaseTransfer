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
        JLabel label = null;
        if (node.getUserObject() instanceof Carpeta) {
            if (expanded) {
                label = new JLabel(
                        node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                        .getClassLoader().getResource
                                ("img/icons/folder-blue-open.png"))), JLabel.CENTER);
            } else {
                label = new JLabel(
                        node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                        .getClassLoader().getResource
                                ("img/icons/folder-blue.png"))), JLabel.CENTER);
            }
        } else if (node.getUserObject() instanceof Servidor) {
            label =  new JLabel(
                    node.getUserObject().toString(), new ImageIcon(Objects.requireNonNull(getClass()
                    .getClassLoader().getResource
                            ("img/icons/application-x-sqlite2.png"))), JLabel.CENTER);
        }
        if (selected) {
            assert label != null;
            label.setBackground(javax.swing.UIManager.getDefaults().getColor("List.selectionBackground"));
            label.setForeground(javax.swing.UIManager.getDefaults().getColor("List.selectionForeground"));
            label.setOpaque(true);
        }
        return label;
    }
}
