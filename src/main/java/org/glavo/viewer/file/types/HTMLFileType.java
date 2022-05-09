package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class HTMLFileType extends TextFileType {

    public static final HTMLFileType TYPE = new HTMLFileType();

    private HTMLFileType() {
        super("html");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".html");
    }
}
