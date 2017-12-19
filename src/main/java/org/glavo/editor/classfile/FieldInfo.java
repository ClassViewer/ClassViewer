package org.glavo.editor.classfile;

import org.glavo.editor.classfile.attribute.AttributeInfo;
import org.glavo.editor.classfile.constant.ConstantPool;
import org.glavo.editor.classfile.jvm.AccessFlagType;

/*
field_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class FieldInfo extends ClassFileComponent {

    {
        u2af ("access_flags", AccessFlagType.AF_FIELD);
        u2cp ("name_index");
        u2cp ("descriptor_index");
        u2   ("attributes_count");
        table("attributes", AttributeInfo.class);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int nameIndex = super.getUInt("name_index");
        if (nameIndex > 0) {
            // todo fix loading java.lang.String from rt.jar
            setDesc(cp.getUtf8String(nameIndex));
        }
    }
    
}
