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
import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;

public class Table<C extends ClassFileComponent> extends ClassFileComponent {
    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        return readFrom(reader, length, f, false);
    }

    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> f, boolean showIndex) throws IOException {
        int offset = reader.getOffset();

        Table<C> table = new Table<>();
        int len = length.getIntValue();
        for (int i = 0; i < len; i++) {
            C c = f.applyChecked(reader);
            if (showIndex) {
                c.setName(StringUtils.formatIndex(i, len));
            }
            table.getChildren().add(c);
        }
        length.intValueProperty().bind(Bindings.size(table.getChildren()));

        table.setLength(reader.getOffset() - offset);
        return table;
    }

    @SuppressWarnings("unchecked")
    public C getEntry(int idx) {
        return (C) getChildren().get(idx);
    }
}
