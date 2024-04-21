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

/*
CONSTANT_Package_info {
    u1 tag;
    u2 name_index;
}
*/
public final class ConstantPackageInfo extends ConstantInfo {
    public ConstantPackageInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> nameIndex) {
        super(tag);
        nameIndex.setName("name_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, nameIndex);
    }

    public CpIndex<ConstantUtf8Info> nameIndex() {
        return component(1);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.map(nameIndex().constantInfoProperty(), ConstantUtf8Info::getDescText);
    }
}
