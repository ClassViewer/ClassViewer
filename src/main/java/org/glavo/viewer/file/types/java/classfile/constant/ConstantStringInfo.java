package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;

/*
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
*/
public final class ConstantStringInfo extends ConstantInfo {
    public ConstantStringInfo(ConstantInfo.Tag tag, CpIndex<ConstantUtf8Info> stringIndex) {
        super(tag);
        stringIndex.setName("string_index");

        //noinspection unchecked
        this.getChildren().setAll(tag, stringIndex);
    }
}
