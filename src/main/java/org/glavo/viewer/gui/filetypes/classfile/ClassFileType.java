package org.glavo.viewer.gui.filetypes.classfile;

import javafx.stage.FileChooser;
import org.glavo.viewer.classfile.ClassFile;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.filetypes.FileType;

import java.net.URL;
import java.util.Objects;

public final class ClassFileType extends FileType {
    public static final ClassFileType Instance = new ClassFileType();

    private ClassFileType() {
        this.filter = new FileChooser.ExtensionFilter("");
    }

    @Override
    public boolean accept(URL url) {
        Objects.requireNonNull(url);
        return url.toString().toLowerCase().endsWith(".class");
    }

    @Override
    public void open(Viewer viewer, URL url, boolean createNewTab) {

    }
}
