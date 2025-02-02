package org.glavo.viewer.classfile.attribute;

import org.glavo.viewer.classfile.ClassFileComponent;

public final class RecordAttribute extends AttributeInfo {
    {
        u2("components_count");
        table("components", RecordComponentInfo.class);
    }

    public static final class RecordComponentInfo extends ClassFileComponent {
        {
            u2("name_index");
            u2("descriptor_index");
            u2("attributes_count");
            table("attributes", AttributeInfo.class);
        }
    }
}
