package org.glavo.viewer.classfile.attribute;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public final class UndefinedAttribute extends AttributeInfo {

    {
        bytes("info");
    }

}
