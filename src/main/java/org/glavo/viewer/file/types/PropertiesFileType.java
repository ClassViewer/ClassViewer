package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class PropertiesFileType extends TextFileType {

    public static final PropertiesFileType TYPE = new PropertiesFileType();

    private PropertiesFileType() {
        super("properties");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("properties");
    }
}
