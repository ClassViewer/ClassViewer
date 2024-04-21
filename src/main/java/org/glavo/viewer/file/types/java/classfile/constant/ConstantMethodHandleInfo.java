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
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.jvm.RefKind;
import org.reactfx.value.Val;

import java.util.function.Function;

/*
CONSTANT_MethodHandle_info {
    u1 tag;
    u1 reference_kind;
    u2 reference_index;
}
*/
public final class ConstantMethodHandleInfo extends ConstantInfo {
    public ConstantMethodHandleInfo(ConstantInfo.Tag tag, U1 referenceKind, CpIndex<ConstantInfo> referenceIndex) {
        super(tag);
        referenceKind.setName("reference_kind");
        referenceIndex.setName("reference_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, referenceKind, referenceIndex);
    }

    public U1 referenceKind() {
        return component(1);
    }

    public CpIndex<ConstantInfo> referenceIndex() {
        return component(2);
    }

    public CpIndex<ConstantInfo> getReferenceIndex() {
        return (CpIndex<ConstantInfo>) getChildren().get(2);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.combine(referenceKind().intValueProperty(), referenceIndex().constantInfoProperty(),
                (kind, info) -> {
                    RefKind k = RefKind.valueOf(kind.intValue());
                    if (k == null || info == null) return null;

                    switch (k) {
                        case REF_getField, REF_getStatic, REF_putField, REF_putStatic -> {
                            if (!(info instanceof ConstantFieldrefInfo))
                                return null;
                        }
                        case REF_invokeVirtual, REF_newInvokeSpecial -> {
                            if (!(info instanceof ConstantMethodrefInfo))
                                return null;
                        }
                        case REF_invokeStatic, REF_invokeSpecial -> {
                            if (!(info instanceof ConstantMethodrefInfo) && !(info instanceof ConstantInterfaceMethodrefInfo))
                                return null;
                        }
                        case REF_invokeInterface -> {
                            if (!(info instanceof ConstantInterfaceMethodrefInfo))
                                return null;
                        }
                    }
                    return Val.map(info.descTextProperty(), text -> text == null ? null : k + "->" + text);
                }).flatMap(Function.identity());
    }
}
