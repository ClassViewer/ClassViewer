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
package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;

import java.util.Arrays;

public class Bytes extends ClassFileComponent {
    private final ObjectProperty<byte[]> values = new SimpleObjectProperty<>();

    public Bytes(byte[] values) {
        this.valuesProperty().addListener((o, oldValue, newValue) -> setLength(newValue.length));
        this.setValues(values);
        this.descProperty().bind(this.lengthProperty().map(it -> new Label("byte[" + it + "]")));
    }

    public ObjectProperty<byte[]> valuesProperty() {
        return values;
    }

    public byte[] getValues() {
        return values.getValue();
    }

    public void setValues(byte[] values) {
        this.values.set(values);
    }

    @Override
    public String contentToString() {
        return Arrays.toString(values.getValue());
    }
}
