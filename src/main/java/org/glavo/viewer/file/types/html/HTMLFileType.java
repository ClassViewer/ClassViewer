package org.glavo.viewer.file.types.html;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.TextFileType;

public class HTMLFileType extends TextFileType {

    public static final HTMLFileType TYPE = new HTMLFileType();

    private HTMLFileType() {
        super("html");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("html");
    }
}
