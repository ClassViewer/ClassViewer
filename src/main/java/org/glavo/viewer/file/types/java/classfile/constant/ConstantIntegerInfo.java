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
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.reactfx.value.Val;

/*
CONSTANT_Integer_info {
    u1 tag;
    u4 bytes;
}
*/
public final class ConstantIntegerInfo extends ConstantValueInfo {
    public ConstantIntegerInfo(ConstantInfo.Tag tag, U4 bytes) {
        super(tag);
        bytes.setName("bytes");

        this.getChildren().setAll(tag, bytes);
    }

    public U4 bytes() {
        return component(1);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.map(bytes().intValueProperty(), it -> String.valueOf(it.intValue()));
    }
}
