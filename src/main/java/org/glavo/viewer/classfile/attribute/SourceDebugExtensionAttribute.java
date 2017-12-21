package org.glavo.viewer.classfile.attribute;

/*
SourceDebugExtension_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 debug_extension[attribute_length];
}
 */
public final class SourceDebugExtensionAttribute extends AttributeInfo {

    {
        bytes("debug_extension");
    }
    
}
