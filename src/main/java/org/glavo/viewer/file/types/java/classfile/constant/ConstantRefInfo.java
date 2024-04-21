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

import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.reactfx.value.Val;

abstract non-sealed class ConstantRefInfo extends ConstantInfo {
    ConstantRefInfo(ConstantInfo.Tag tag, CpIndex<ConstantClassInfo> classIndex, CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex) {
        super(tag);
        classIndex.setName("class_index");
        nameAndTypeIndex.setName("name_and_type_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, classIndex, nameAndTypeIndex);
    }


    public CpIndex<ConstantClassInfo> classIndex() {
        return component(1);
    }

    public CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex() {
        return component(2);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.combine(Val.flatMap(classIndex().constantInfoProperty(), it -> it.nameIndex().constantInfoProperty()), nameAndTypeIndex().constantInfoProperty(),
                (cls, nt) -> {
                    if (cls == null || cls.getDescText() == null || nt == null || nt.getDescText() == null) return null;
                    return cls.getDescText().replace('/', '.') + "." + nt.getDescText();
                });
    }
}
