package org.glavo.editor.gui.support;

import javafx.scene.image.Image;
import javafx.stage.FileChooser.ExtensionFilter;
import org.glavo.editor.classfile.ClassFile;
import org.glavo.editor.common.FileComponent;

/**
 * Supported file types.
 */
public enum FileType {

    JAVA_JAR("/jar.png", "Java JAR", "*.jar"),
    JAVA_CLASS("/java.png", "Java Class", "*.class"),
    UNKNOWN("/file.png", "Unknown", "*.*"),;

    public final Image icon;
    public final ExtensionFilter filter;

    private FileType(String icon, String description, String extension) {
        this.icon = ImageHelper.loadImage(icon);
        this.filter = new ExtensionFilter(description, extension);
    }

    public static FileType typeOf(FileComponent root) {
        return JAVA_CLASS;

    }

}
