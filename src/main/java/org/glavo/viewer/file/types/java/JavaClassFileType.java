package org.glavo.viewer.file.types.java;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.java.classfile.ClassFile;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.ByteList;
import org.glavo.viewer.util.TaskUtils;

import java.io.InputStream;

public class JavaClassFileType extends BinaryFileType {
    public static final JavaClassFileType TYPE = new JavaClassFileType();

    private JavaClassFileType() {
        super("java-class");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("class") || path.getFileNameExtension().equals("sig");
    }

    @Override
    protected void openContent(FileTab tab, FileHandle handle, ByteList bytes) {
        handle.close();

        TaskUtils.submit(new Task<ClassFile>() {
            @Override
            protected ClassFile call() throws Exception {
                try (InputStream input = bytes.openInputStream()) {
                    return ClassFile.readFrom(new ClassFileReader(input));
                }
            }

            @Override
            protected void succeeded() {
                ClassFileTreeView tree = new ClassFileTreeView(tab);
                tree.setRoot(getValue());
                tab.setSideBar(tree);
            }

            @Override
            protected void failed() {
                tab.setSideBar(new StackPane(new Label(I18N.getString("file.wrongFormat"))));
            }
        });

    }
}
