package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class JavaSourceFileType extends TextFileType {

    public static final JavaSourceFileType TYPE = new JavaSourceFileType();

    private JavaSourceFileType() {
        super("java");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("java");
    }
}
