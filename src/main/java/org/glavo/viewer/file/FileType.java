package org.glavo.viewer.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import javafx.scene.image.Image;
import org.glavo.viewer.file.types.*;
import org.glavo.viewer.resources.Images;

import java.util.*;


public abstract class FileType {
    private final String name;
    private final Image image;

    protected FileType(String name) {
        this(name, Images.loadImage("fileTypes/file-" + name + ".png"));
    }

    protected FileType(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public boolean isContainer() {
        return this instanceof ContainerFileType;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
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

    public static List<FileType> getTypes() {
        return Hole.types;
    }

    @JsonCreator
    public static FileType ofName(String name) {
        for (FileType type : getTypes()) {
            if (name.equals(type.name)) {
                return type;
            }
        }

        return new UnknownFileType(name);
    }

    private static final class Hole {
        @SuppressWarnings("Java9CollectionFactory")
        private static final List<FileType> types = Collections.unmodifiableList(Arrays.asList(
                new FolderType(),

                new BinaryFileType(),
                new JImageFileType(),
                new ArchiveFileType(),

                new TextFileType(),
                new ManifestFileType(),
                new PropertiesFileType()
        ));
    }

}
