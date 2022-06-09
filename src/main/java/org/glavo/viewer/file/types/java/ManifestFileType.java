package org.glavo.viewer.file.types.java;

import org.glavo.viewer.file.OldFilePath;
import org.glavo.viewer.file.types.TextFileType;

public class ManifestFileType extends TextFileType {
    public static final ManifestFileType TYPE = new ManifestFileType();

    private ManifestFileType() {
        super("manifest");
    }

    @Override
    public boolean check(OldFilePath path) {
        return path.getFileNameExtension().equals("mf");
    }
}
