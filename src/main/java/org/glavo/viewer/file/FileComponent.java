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
package org.glavo.viewer.file;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TreeItem;

public abstract class FileComponent<C extends FileComponent<C>> extends TreeItem<C> {
    private final IntegerProperty offset = new SimpleIntegerProperty();
    private final IntegerProperty length = new SimpleIntegerProperty();

    @SuppressWarnings("unchecked")
    protected FileComponent() {
        this.setValue((C) this);
    }

    public IntegerProperty offsetProperty() {
        return offset;
    }

    public int getOffset() {
        return offsetProperty().get();
    }

    public void setOffset(int offset) {
        this.offsetProperty().set(offset);
    }

    public IntegerProperty lengthProperty() {
        return length;
    }

    public int getLength() {
        return lengthProperty().get();
    }

    public void setLength(int length) {
        this.lengthProperty().set(length);
    }

    public String contentToString() {
        return "";
    }

    @Override
    public String toString() {
        return "%s[value=%s, offset=%s, length=%s]".formatted(this.getClass().getSimpleName(), contentToString(), getOffset(), getLength());
    }
}
