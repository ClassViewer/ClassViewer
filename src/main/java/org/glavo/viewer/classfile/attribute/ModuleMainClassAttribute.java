package org.glavo.viewer.classfile.attribute;

/*
ModuleMainClass_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 main_class_index;
}
 */
public final class ModuleMainClassAttribute extends AttributeInfo {

    {
        u2cp("main_class_index");
    }

}
