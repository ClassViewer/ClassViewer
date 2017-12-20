package org.glavo.viewer.gui.support;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Supported file types.
 */
public enum FileType {

    JAVA_JAR("/icons/JarFile.png", "Java JAR File (*.jar)", "*.jar"),
    JAVA_CLASS("/icons/ClassFile.png", "Java Class File (*.class)", "*.class"),
    UNKNOWN("/icons/UnknownFile.png", "Unknown", "*.*"),;

    public static final FileType[] fileTypes = {
            JAVA_JAR,
            JAVA_CLASS
    };

    public static final ExtensionFilter allFiles;

    static {
        ArrayList<String> allExtendsions = new ArrayList<>();

        for (FileType type : fileTypes) {
            allExtendsions.addAll(Arrays.asList(type.extendsions));
        }

        allFiles = new ExtensionFilter("All files", allExtendsions.toArray(new String[allExtendsions.size()]));
    }

    public final Image icon;
    public final String[] extendsions;
    public final ExtensionFilter filter;

    FileType(String icon, String description, String... extension) {
        this.icon = ImageUtils.loadImage(icon);
        this.filter = new ExtensionFilter(description, extension);
        this.extendsions = extension;
    }

    public ImageView imageView() {
        return new ImageView(icon);
    }
}
