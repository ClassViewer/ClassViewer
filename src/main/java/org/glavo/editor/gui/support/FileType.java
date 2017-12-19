package org.glavo.editor.gui.support;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Supported file types.
 */
public enum FileType {

    JAVA_JAR("/JarFile.png", "Java JAR", "*.jar"),
    JAVA_CLASS("/ClassFile.png", "Java Class", "*.class"),
    UNKNOWN("/UnknownFile.png", "Unknown", "*.*"),;

    public final Image icon;
    public final ExtensionFilter filter;

    FileType(String icon, String description, String extension) {
        this.icon = ImageUtils.loadImage(icon);
        this.filter = new ExtensionFilter(description, extension);
    }

    public ImageView imageView() {
        return new ImageView(icon);
    }
}
