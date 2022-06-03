package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
SourceFile_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 sourcefile_index;
}
 */
public class SourceFileAttribute extends AttributeInfo {
    public SourceFileAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, CpIndex<ConstantUtf8Info> sourcefileIndex) {
        super(attributeNameIndex, attributeLength);

        sourcefileIndex.setName("sourcefile_index");
        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, sourcefileIndex);
    }
}
