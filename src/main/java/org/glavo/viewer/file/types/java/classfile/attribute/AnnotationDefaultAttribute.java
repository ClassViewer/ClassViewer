package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
AnnotationDefault_attribute {
    u2            attribute_name_index;
    u4            attribute_length;
    element_value default_value;
}
 */
public class AnnotationDefaultAttribute extends AttributeInfo {
    AnnotationDefaultAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, RuntimeAnnotationsAttribute.ElementValue defaultValue) {
        super(attributeNameIndex, attributeLength);
        defaultValue.setName("default_value");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, defaultValue);
    }
}
