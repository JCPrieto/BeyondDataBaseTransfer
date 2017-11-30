package es.jklabs.gui.utilidades.filtro;

import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Objects;

public class JSonFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        return Objects.equals(FilenameUtils.getExtension(f.getName()), "json");
    }

    @Override
    public String getDescription() {
        return "Archivo JSon (*.json)";
    }
}
