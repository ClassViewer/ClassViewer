package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;

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
}
