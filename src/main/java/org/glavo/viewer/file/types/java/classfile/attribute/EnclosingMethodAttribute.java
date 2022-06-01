package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantNameAndTypeInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
EnclosingMethod_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 class_index;
    u2 method_index;
}
 */
public class EnclosingMethodAttribute extends AttributeInfo {
    EnclosingMethodAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                             CpIndex<ConstantClassInfo> classIndex, CpIndex<ConstantNameAndTypeInfo> methodIndex) {
        super(attributeNameIndex, attributeLength);
        classIndex.setName("class_index");
        methodIndex.setName("method_index");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, classIndex, methodIndex);
    }
}
