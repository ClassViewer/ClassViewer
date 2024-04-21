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
package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ConstantPool extends ClassFileComponent {

    public static ConstantPool readFrom(ClassFileReader reader, U2 cpCount) throws IOException {
        int count = cpCount.getIntValue();
        int offset = reader.getOffset();
        ObservableList<ConstantInfo> constants = FXCollections.observableList(new ArrayList<>(count));
        constants.add(null);

        // The constant_pool table is indexed from 1 to constant_pool_count - 1
        for (int i = 1; i < count; i++) {
            ConstantInfo c = ConstantInfo.readFrom(reader);
            c.setName(StringUtils.formatIndex(i, count));
            constants.add(c);
            // http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5
            // All 8-byte constants take up two entries in the constant_pool table of the class file.
            // If a CONSTANT_Long_info or CONSTANT_Double_info structure is the item in the constant_pool
            // table at index n, then the next usable item in the pool is located at index n+2.
            // The constant_pool index n+1 must be valid but is considered unusable.
            if (c instanceof ConstantLongInfo || c instanceof ConstantDoubleInfo) {
                constants.add(null);
                i++;
            }
        }

        ConstantPool constantPool = new ConstantPool(constants);
        constantPool.setLength(reader.getOffset() - offset);
        cpCount.intValueProperty().bind(Bindings.size(constants));
        return constantPool;
    }


    private final ObservableList<ConstantInfo> constants;

    private ConstantPool(ObservableList<ConstantInfo> constants) {
        this.constants = constants;
        this.setName("constant_pool");
        Bindings.bindContent(this.getChildren(), new FilteredList<>(constants, Objects::nonNull));
    }

    public ObservableList<ConstantInfo> getConstants() {
        return constants;
    }

    public ConstantInfo getConstant(int index) {
        return index > 0 && index < constants.size() ? constants.get(index) : null;
    }
}
