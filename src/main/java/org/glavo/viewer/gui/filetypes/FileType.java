package org.glavo.viewer.gui.filetypes;

import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.ViewerTab;
import org.glavo.viewer.gui.filetypes.binary.BinaryFileType;
import org.glavo.viewer.gui.filetypes.classfile.ClassFileType;
import org.glavo.viewer.gui.filetypes.jar.JarFileType;
import org.glavo.viewer.gui.filetypes.jmod.JModFileType;
import org.glavo.viewer.gui.folder.FolderType;

import java.net.URL;

public abstract class FileType {
    public static final FileType[] fileTypes = {
            ClassFileType.Instance,
            JarFileType.Instance,
            JModFileType.Instance,
            BinaryFileType.Instance,
            FolderType.Instance
    };

    public static final FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter(
            "All files", "*.*"
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

    public abstract ViewerTab open(Viewer viewer, URL url) throws Exception;

    @Override
    public abstract String toString();
}
