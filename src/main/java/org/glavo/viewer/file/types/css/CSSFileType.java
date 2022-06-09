package org.glavo.viewer.file.types.css;

import org.glavo.viewer.file.LocalFilePath;
import org.glavo.viewer.file.types.TextFileType;

public class CSSFileType extends TextFileType {

    public static final CSSFileType TYPE = new CSSFileType();

    private CSSFileType() {
        super("css");
    }

    @Override
    public boolean check(LocalFilePath path) {
        return path.getFileNameExtension().equals("css");
    }
}
