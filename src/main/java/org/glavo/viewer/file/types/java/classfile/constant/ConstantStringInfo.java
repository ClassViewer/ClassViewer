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
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

/*
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
*/
public final class ConstantStringInfo extends ConstantValueInfo {
    public ConstantStringInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> stringIndex) {
        super(tag);
        stringIndex.setName("string_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, stringIndex);
        this.descProperty().bind(Val.map(stringIndex.constantInfoProperty(), it ->
                it == null ? null : StringUtils.cutTextNode(it.getDescText(), Label::new)));
    }

    public CpIndex<ConstantUtf8Info> stringIndex() {
        return component(1);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.map(stringIndex().constantInfoProperty(), ConstantUtf8Info::getDescText);
    }
}
