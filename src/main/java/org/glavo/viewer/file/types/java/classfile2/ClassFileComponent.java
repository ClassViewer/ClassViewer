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
package org.glavo.viewer.file.types.java.classfile2;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

public abstract class ClassFileComponent extends TreeItem<String> {
    protected Node icon;
    protected String name;
    protected Node desc;

    protected void updateGraphic(ClassFileTreeView.Cell cell) {
        cell.setGraphic(icon);
        cell.setText("TODO");
    }
}
