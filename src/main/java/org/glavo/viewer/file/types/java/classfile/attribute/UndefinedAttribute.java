package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public class UndefinedAttribute extends Attribute {
    public UndefinedAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {
        super(attributeNameIndex, attributeLength, info);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, info);
    }
}
