package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class JavaClassFileType extends BinaryFileType {
    public JavaClassFileType() {
        super("java-class");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".class") || path.getFileName().endsWith(".sig");
    }
}
