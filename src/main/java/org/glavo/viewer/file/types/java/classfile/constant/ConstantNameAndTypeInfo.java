package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;

/*
CONSTANT_NameAndType_info {
    u1 tag;
    u2 name_index;
    u2 descriptor_index;
}
*/
public final class ConstantNameAndTypeInfo extends ConstantInfo {
    public ConstantNameAndTypeInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> nameIndex, CpIndex<ConstantUtf8Info> descriptorIndex) {
        super(tag);
        nameIndex.setName("name_index");
        descriptorIndex.setName("descriptor_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, nameIndex, descriptorIndex);
    }
}
