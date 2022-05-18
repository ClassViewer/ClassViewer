package org.glavo.viewer.file.types.json;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.TextFileType;

public class Json5FileType extends TextFileType {

    public static final Json5FileType TYPE = new Json5FileType();

    private Json5FileType() {
        super("json5", JsonFileType.TYPE.getImage());
        this.highlighter = JsonFileType.TYPE.getHighlighter();
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("json5");
    }
}
