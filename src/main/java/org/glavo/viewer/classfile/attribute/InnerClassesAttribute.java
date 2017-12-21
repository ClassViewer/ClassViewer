package org.glavo.viewer.classfile.attribute;

import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.jvm.AccessFlagType;

/*
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {   u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
 */
public final class InnerClassesAttribute extends AttributeInfo {

    {
        u2   ("number_of_classes");
        table("classes", InnerClassInfo.class);
    }
    
    
    public static class InnerClassInfo extends ClassFileComponent {

        {
            u2cp("inner_class_info_index");
            u2cp("outer_class_info_index");
            u2cp("inner_name_index");
            u2af("inner_class_access_flags", AccessFlagType.AF_NESTED_CLASS);
        }
        
    }
    
}
