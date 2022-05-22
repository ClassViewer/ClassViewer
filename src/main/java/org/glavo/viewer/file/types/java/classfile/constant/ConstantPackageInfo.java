package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U2;

/*
CONSTANT_Package_info {
    u1 tag;
    u2 name_index;
}
*/
public final class ConstantPackageInfo extends ConstantInfo {
    public ConstantPackageInfo(ConstantInfo.Tag tag, U2 nameIndex) {
        super(tag);
        nameIndex.setName("name_index");

        this.getChildren().setAll(tag, nameIndex);
    }
}
