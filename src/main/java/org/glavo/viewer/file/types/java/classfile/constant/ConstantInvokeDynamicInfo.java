package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.reactfx.value.Val;

/*
CONSTANT_InvokeDynamic_info {
    u1 tag;
    u2 bootstrap_method_attr_index;
    u2 name_and_type_index;
}
*/
public final class ConstantInvokeDynamicInfo extends ConstantInfo {
    public ConstantInvokeDynamicInfo(ConstantInfo.Tag tag, U2 bootstrapMethodAttrIndex, CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex) {
        super(tag);
        bootstrapMethodAttrIndex.setName("bootstrap_method_attr_index");
        nameAndTypeIndex.setName("name_and_type_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }

    public U2 bootstrapMethodAttrIndex() {
        return component(1);
    }

    public CpIndex<ConstantNameAndTypeInfo> nameAndTypeIndex() {
        return component(2);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.map(nameAndTypeIndex().constantInfoProperty(),
                info -> {
                    if (info == null || info.getDescText() == null) return null;
                    return info.getDescText();
                });
    }
}
