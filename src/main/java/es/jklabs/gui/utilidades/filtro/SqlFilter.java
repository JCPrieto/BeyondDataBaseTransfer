package es.jklabs.gui.utilidades.filtro;

import es.jklabs.utilidades.Mensajes;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SqlFilter extends FileFilter {

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().toLowerCase().endsWith(".sql");
    }

    @Override
    public String getDescription() {
        return Mensajes.getMensaje("file.chooser.sql");
    }
}
