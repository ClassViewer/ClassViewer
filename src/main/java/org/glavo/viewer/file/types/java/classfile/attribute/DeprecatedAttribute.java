package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

public class DeprecatedAttribute extends AttributeInfo {
    public DeprecatedAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength);
    }
}
