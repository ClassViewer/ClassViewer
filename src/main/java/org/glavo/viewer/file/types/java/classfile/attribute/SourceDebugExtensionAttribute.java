package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.Bytes;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

public class SourceDebugExtensionAttribute extends AttributeInfo {
    SourceDebugExtensionAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, Bytes info) {
        super(attributeNameIndex, attributeLength);

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, info);
    }
}
