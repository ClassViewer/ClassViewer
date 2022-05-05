package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class ManifestFileType extends TextFileType {
    public static final ManifestFileType TYPE = new ManifestFileType();

    private ManifestFileType() {
        super("manifest");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".mf") || path.getFileName().endsWith(".MF");
    }
}
