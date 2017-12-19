package org.glavo.viewer.classfile.attribute;


import org.glavo.viewer.classfile.ClassFileComponent;

/*
attribute_info {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 info[attribute_length];
}
 */
public abstract class AttributeInfo extends ClassFileComponent {

    {
        u2("attribute_name_index");
        u4("attribute_length");
    }

}
