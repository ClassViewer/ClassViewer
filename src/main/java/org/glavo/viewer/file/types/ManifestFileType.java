package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

import java.util.Locale;

public class ManifestFileType extends TextFileType {
    public ManifestFileType() {
        super("manifest");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".mf") || path.getFileName().endsWith(".MF");
    }
}
