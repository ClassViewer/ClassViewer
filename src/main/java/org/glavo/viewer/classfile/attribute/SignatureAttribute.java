package org.glavo.viewer.classfile.attribute;

/*
Signature_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 signature_index;
}
 */
public final class SignatureAttribute extends AttributeInfo {

    {
        u2cp("signature_index");
    }
    
}
