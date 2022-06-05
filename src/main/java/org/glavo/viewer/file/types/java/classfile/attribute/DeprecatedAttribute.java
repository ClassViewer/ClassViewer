package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
Deprecated_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
}
 */
public class DeprecatedAttribute extends AttributeInfo {
    DeprecatedAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength);
    }
}
