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

import javafx.scene.control.TreeItem;
import org.glavo.viewer.file.types.BinaryFileType;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;

import java.util.Set;

public final class JavaClassFileType extends BinaryFileType {
    public static final JavaClassFileType TYPE = new JavaClassFileType();

    private JavaClassFileType() {
        super("java-class", Set.of("class", "sig"));
    }

    // TODO
//    @Override
//    protected void openContent(FileTab tab, FileHandle handle, ByteSeq bytes) {
//        handle.close();
//
//        tab.setSideBar(new StackPane(new ProgressIndicator()));
//        TaskUtils.submit(new Task<ClassFileTreeView>() {
//            private ClassFileReader reader;
//
//            @Override
//            protected ClassFileTreeView call() throws Exception {
//                ClassFileTreeView view = new ClassFileTreeView(tab);
//                ClassFile file;
//                try (InputStream input = new ByteSeqInputStream(bytes)) {
//                    file = ClassFile.readFrom(view, reader = new ClassFileReader(input));
//                }
//
//                loadDesc(view, file);
//                return view;
//            }
//
//            @Override
//            protected void succeeded() {
//                reader = null;
//                tab.setSideBar(getValue());
//            }
//
//            @Override
//            protected void failed() {
//                if (reader != null) {
//                    LOGGER.warning("Failed to parse Java Class file (offset=" + Integer.toHexString(reader.getOffset()) + ")", getException());
//                    reader = null;
//                } else {
//                    LOGGER.warning("Failed to parse Java Class file", getException());
//                }
//                tab.setSideBar(new StackPane(new Label(I18N.getString("file.wrongFormat"))));
//            }
//        });
//
//    }

    private static void loadDesc(ClassFileTreeView view, ClassFileComponent component) {
        component.loadDesc(view);
        for (TreeItem<ClassFileComponent> child : component.getChildren()) {
            loadDesc(view, child.getValue());
        }
    }
}
