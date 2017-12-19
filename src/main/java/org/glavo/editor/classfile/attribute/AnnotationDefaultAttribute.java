package org.glavo.editor.classfile.attribute;

import org.glavo.editor.classfile.attribute.RuntimeVisibleAnnotationsAttribute.ElementValue;

/*
AnnotationDefault_attribute {
    u2            attribute_name_index;
    u4            attribute_length;
    element_value default_value;
}
 */
public class AnnotationDefaultAttribute extends AttributeInfo {

    {
        add("default_value", new ElementValue());
    }

}
