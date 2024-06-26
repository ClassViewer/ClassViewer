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
package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;

public class ClassFileTreeView extends TreeView<ClassFileComponent> {

    public ClassFileTreeView() {
        this.setCellFactory(view -> new Cell());
        this.getStyleClass().add("monospaced-font");
    }

    public ClassFile getClassFile() {
        return (ClassFile) getRoot().getValue();
    }

    public ConstantPool getConstantPool() {
        return getClassFile().getConstantPool();
    }

    public static final class Cell extends TreeCell<ClassFileComponent> {
        @Override
        protected void updateItem(ClassFileComponent item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox box = new HBox();
                Node icon = item.getIcon();
                String name = item.getName();
                Node desc = item.getDesc();
                Tooltip tooltip = item.getTooltip();

                if (icon != null) box.getChildren().add(icon);
                if (name != null) box.getChildren().add(new Label(desc == null ? name : name + ":"));
                if (desc != null) box.getChildren().add(desc);
                if (tooltip != null) Tooltip.install(box, tooltip);

                setText(null);
                setGraphic(box);
            }
        }
    }
}
