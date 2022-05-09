package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class CSSFileType extends TextFileType {

    public static final CSSFileType TYPE = new CSSFileType();

    private CSSFileType() {
        super("css");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".css");
    }
}
