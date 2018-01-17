package org.glavo.viewer.gui.filetypes;

import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.filetypes.classfile.ClassFileType;
import org.glavo.viewer.gui.filetypes.jar.JarFileType;

import java.net.URL;

public abstract class FileType {
    public static final FileType[] fileTypes = {
            ClassFileType.Instance,
            JarFileType.Instance
    };

    public static final FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter(
            "All files (*.class, *.jar, *.zip)", "*.class", "*.jar", "*.zip"
    );

    public static FileType valueOf(String name) {
        for (FileType fileType : fileTypes) {
            if (fileType.toString().equals(name)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("No Such FileType: " + name);
    }

    public static FileType typeOf(URL url) {
        for (FileType type : fileTypes) {
            if (type.accept(url)) {
                return type;
            }
        }
        return null;
    }

    public FileChooser.ExtensionFilter filter = null;

    public Image icon = null;

    public abstract boolean accept(URL url);

    public abstract Tab open(Viewer viewer, URL url) throws Exception;
}
