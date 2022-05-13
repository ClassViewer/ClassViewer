package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class JsonFileType extends TextFileType {

    public static final JsonFileType TYPE = new JsonFileType();

    private JsonFileType() {
        super("json");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("json");
    }
}
