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

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaType;
import org.glavo.viewer.file.types.java.classfile.jvm.type.JavaTypes;
import org.reactfx.value.Val;

/*
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
*/
public final class ConstantNameAndTypeInfo extends ConstantInfo {
    private final ObjectProperty<JavaType> type = new SimpleObjectProperty<>();

    public ConstantNameAndTypeInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> nameIndex, CpIndex<ConstantUtf8Info> descriptorIndex) {
        super(tag);
        nameIndex.setName("name_index");
        descriptorIndex.setName("descriptor_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, nameIndex, descriptorIndex);
    }

    public CpIndex<ConstantUtf8Info> nameIndex() {
        return component(1);
    }

    public CpIndex<ConstantUtf8Info> descriptorIndex() {
        return component(2);
    }

    public ObservableValue<JavaType> typeProperty() {
        return type;
    }

    public JavaType getType() {
        return type.getValue();
    }

    @Override
    protected ObservableValue<String> initDescText() {
        type.bind(Val.map(descriptorIndex().constantInfoProperty(), descriptor -> {
            if (descriptor == null || descriptor.getDescText() == null) return null;
            try {
                return JavaTypes.parseDescriptor(descriptor.getDescText());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }));

        return Val.combine(nameIndex().constantInfoProperty(), this.typeProperty(),
                (name, type) -> {
                    if (name == null || name.getDescText() == null || type == null) return null;
                    return type.isMethodType() ? name.getDescText() + type.getQualified() : name.getDescText() + ": " + type.getQualified();
                });
    }
}
