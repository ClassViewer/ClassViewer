package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;

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
}
