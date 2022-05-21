package org.glavo.viewer.file.types.java.classfile.constant;

import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Module_info {
    u1 tag;
    u2 name_index;
}
*/
public final class ConstantModuleInfo extends ConstantInfo {
    public ConstantModuleInfo(U1 tag, U2 nameIndex) {
        super(tag);
        nameIndex.setName("name_index");

        this.getChildren().setAll(tag, nameIndex);
    }
}