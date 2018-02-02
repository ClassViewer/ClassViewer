package org.glavo.viewer.classfile.attribute;

import org.glavo.viewer.classfile.ClassFileComponent;

/*
MethodParameters_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 parameters_count;
    {   u2 name_index;
        u2 access_flags;
    } parameters[parameters_count];
}
 */
public class MethodParametersAttribute extends AttributeInfo {

    {
        u1   ("parameters_count");
        table("parameters", ParameterInfo.class);
    }

    
    public static class ParameterInfo extends ClassFileComponent {

        {
            u2("name_index");
            u2("access_flags");
        }
        
    }
    
}
