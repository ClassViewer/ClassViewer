package org.glavo.viewer.file.types.java;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.TextFileType;

public class ManifestFileType extends TextFileType {
    public static final ManifestFileType TYPE = new ManifestFileType();

    private ManifestFileType() {
        super("manifest");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("mf");
    }
}
