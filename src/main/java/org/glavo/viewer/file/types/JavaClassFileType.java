package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class JavaClassFileType extends BinaryFileType {
    public static final JavaClassFileType TYPE = new JavaClassFileType();

    private JavaClassFileType() {
        super("java-class");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".class") || path.getFileName().endsWith(".sig");
    }
}
