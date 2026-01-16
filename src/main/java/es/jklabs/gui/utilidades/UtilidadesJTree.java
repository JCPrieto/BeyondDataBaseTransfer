package es.jklabs.gui.utilidades;

import es.jklabs.json.configuracion.server.Carpeta;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class UtilidadesJTree {

    private UtilidadesJTree() {

    }

    public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public static void addCarpeta(DefaultMutableTreeNode padre, Carpeta carpeta) {
        DefaultMutableTreeNode hijo = new DefaultMutableTreeNode(carpeta);
        padre.add(hijo);
        addElementos(carpeta, hijo);
    }

    public static void addElementos(Carpeta carpetaRaiz, DefaultMutableTreeNode padre) {
        carpetaRaiz.getCarpetas().forEach(c -> addCarpeta(padre, c));
        carpetaRaiz.getServidores().forEach(s -> padre.add(new DefaultMutableTreeNode(s)));
    }
}
