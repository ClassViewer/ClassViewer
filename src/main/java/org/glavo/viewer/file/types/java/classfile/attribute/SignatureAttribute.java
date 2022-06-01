package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
Signature_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 signature_index;
}
 */
public class SignatureAttribute extends AttributeInfo {

    SignatureAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength, CpIndex<ConstantUtf8Info> signatureIndex) {
        super(attributeNameIndex, attributeLength);
        signatureIndex.setName("signature_index");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, signatureIndex);
    }
}
