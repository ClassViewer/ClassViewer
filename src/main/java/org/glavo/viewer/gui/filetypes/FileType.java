package org.glavo.viewer.gui.filetypes;

import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.filetypes.classfile.ClassFileType;

import java.net.URL;

public abstract class FileType {
    public static final FileType[] fileTypes = {
            ClassFileType.Instance
    };

    public static final FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter(
            "All files (*.class)", "*.class"
    );

    public static FileType valueOf(String name) {
        for (FileType fileType : fileTypes) {
            if (fileType.toString().equals(name)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("No Such FileType: " + name);
    }

    public FileChooser.ExtensionFilter filter = null;

    public Image icon = null;

    public abstract boolean accept(URL url);

    public abstract Tab open(Viewer viewer, URL url) throws Exception;
}
