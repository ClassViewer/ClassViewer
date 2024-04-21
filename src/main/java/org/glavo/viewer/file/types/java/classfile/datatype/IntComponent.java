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

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;

public abstract class IntComponent extends ClassFileComponent {
    private final IntegerProperty intValue = new SimpleIntegerProperty();

    protected IntComponent(int length, int value) {
        this.setLength(length);
        this.setIntValue(value);
        this.descProperty().bind(Bindings.createObjectBinding(() -> new Label(contentToString()), intValueProperty()));
    }

    public IntegerProperty intValueProperty() {
        return intValue;
    }

    public int getIntValue() {
        return intValue.get();
    }

    public void setIntValue(int intValue) {
        this.intValue.set(intValue);
    }

    @Override
    public abstract String contentToString();
}
