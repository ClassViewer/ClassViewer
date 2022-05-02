package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class PropertiesFileType extends TextFileType {
    public PropertiesFileType() {
        super("properties");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".properties");
    }
}
