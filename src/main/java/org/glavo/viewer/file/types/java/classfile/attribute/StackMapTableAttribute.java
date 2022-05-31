package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
StackMapTable_attribute {
    u2              attribute_name_index;
    u4              attribute_length;
    u2              number_of_entries;
    stack_map_frame entries[number_of_entries];
}
 */
public class StackMapTableAttribute extends AttributeInfo {

    // TODO
    public StackMapTableAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {
        super(attributeNameIndex, attributeLength);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, info);
    }
}
