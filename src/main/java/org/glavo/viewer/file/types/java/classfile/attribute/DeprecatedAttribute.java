package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
Deprecated_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
}
 */
public class DeprecatedAttribute extends AttributeInfo {
    public static DeprecatedAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new DeprecatedAttribute(attributeNameIndex, attributeLength);
        return attribute;
    }

    private DeprecatedAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength);
    }
}
