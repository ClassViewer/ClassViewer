/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.glavo.viewer.file.FileComponent;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.java.classfile.ClassFile;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.HexPane;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

public final class JavaClassFileType extends BinaryFileType {
    public static final JavaClassFileType TYPE = new JavaClassFileType();

    private JavaClassFileType() {
        super("java-class", Set.of("class", "sig"));
    }

    @Override
    public boolean hasSideBar() {
        return true;
    }

    @Override
    protected Node openSideBar(FileTab tab, HexPane hexPane, byte[] bytes) throws Throwable {
        ClassFileTreeView view = new ClassFileTreeView(tab);
        ClassFile file;
        try (InputStream input = new ByteArrayInputStream(bytes)) {
            ClassFileReader reader = new ClassFileReader(input);
            file = ClassFile.readFrom(view, reader);
        }

        loadDesc(view, file);
        view.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getParent() != null) {
                ClassFileComponent cc = newValue.getValue();
                hexPane.setStatus(cc.toString());
                if (cc.getLength() > 0) {
                    hexPane.select(cc.getOffset(), cc.getLength());
                }
            }
        });
        return view;
    }

    private static void loadDesc(ClassFileTreeView view, ClassFileComponent component) {
        component.loadDesc(view);
        for (TreeItem<ClassFileComponent> child : component.getChildren()) {
            loadDesc(view, child.getValue());
        }
    }
}
