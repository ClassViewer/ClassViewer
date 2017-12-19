package org.glavo.editor.classfile;

import org.glavo.editor.FileData;
import org.glavo.editor.FileType;

import java.nio.file.Path;

public class JavaClassFile implements FileType<ClassFile> {
    @Override
    public ClassFile parse(Path path) {
        return null;//todo
    }
}
