package org.glavo.viewer.file.types.java;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.BinaryFileType;

public class JavaClassFileType extends BinaryFileType {
    public static final JavaClassFileType TYPE = new JavaClassFileType();

    private JavaClassFileType() {
        super("java-class");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("class") || path.getFileNameExtension().equals("sig");
    }
}
