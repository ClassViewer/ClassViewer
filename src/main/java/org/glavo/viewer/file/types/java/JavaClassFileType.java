package org.glavo.viewer.file.types.java;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.java.classfile.ClassFile;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
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

        TaskUtils.submit(new Task<ClassFileTreeView>() {
            @Override
            protected ClassFileTreeView call() throws Exception {
                ClassFile file;
                try (InputStream input = bytes.openInputStream()) {
                    file = ClassFile.readFrom(new ClassFileReader(input));
                }
                ClassFileTreeView view = new ClassFileTreeView(tab);
                view.setRoot(file);
                loadDesc(view, file);
                return view;
            }

            @Override
            protected void succeeded() {
                tab.setSideBar(getValue());
            }

            @Override
            protected void failed() {
                tab.setSideBar(new StackPane(new Label(I18N.getString("file.wrongFormat"))));
            }
        });

    }

    private static void loadDesc(ClassFileTreeView view, ClassFileComponent component) {
        component.loadDesc(view);
        for (TreeItem<ClassFileComponent> child : component.getChildren()) {
            loadDesc(view, child.getValue());
        }
    }
}
