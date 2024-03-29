package org.glavo.viewer.file.types.yaml;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.TextFileType;

public class YAMLFileType extends TextFileType {

    public static final YAMLFileType TYPE = new YAMLFileType();

    private YAMLFileType() {
        super("yaml");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("yaml") || path.getFileNameExtension().equals("yml");
    }
}
