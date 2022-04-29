package org.glavo.viewer.file;

import javafx.scene.image.Image;
import org.glavo.viewer.resources.Images;

import java.util.*;

public abstract class FileType {
    /*
    FOLDER(true, Images.loadImage("folder.png")),
    ARCHIVE(true),
    JIMAGE(true, ARCHIVE.image),
    JAVA_CLASS,
    MANIFEST,
    PROPERTIES,
    TEXT,
    UNKNOWN(Images.loadImage("file.png"));
     */

    private final String name;
    private final Image image;

    protected FileType(String name) {
        this(name, Images.loadImage("fileTypes/file-" + name.toLowerCase(Locale.ROOT).replace('_', '-') + ".png"));
    }

    protected FileType(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public boolean isContainer() {
        return this instanceof ContainerFileType;
    }

    public Image getImage() {
        return image;
    }

    private static final class Hole {
        @SuppressWarnings("Java9CollectionFactory")
        private static final List<FileType> types = Collections.unmodifiableList(Arrays.asList(
                new FolderType(),
                new BinaryFileType(),
                new TextFileType()
        ));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FileType)) {
            return false;
        }

        return this.name.equals(((FileType) obj).name);
    }

    @Override
    public String toString() {
        return name;
    }
}
