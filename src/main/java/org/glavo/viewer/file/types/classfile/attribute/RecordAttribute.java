package org.glavo.viewer.file.types.classfile.attribute;

import org.glavo.viewer.file.types.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.classfile.constant.ConstantPool;

public final class RecordAttribute extends AttributeInfo {
    {
        u2("components_count");
        table("components", RecordComponentInfo.class);
    }

    public static final class RecordComponentInfo extends ClassFileComponent {
        {
            u2cp ("name_index");
            u2cp ("descriptor_index");
            u2   ("attributes_count");
            table("attributes", AttributeInfo.class);
        }

        @Override
        protected void postRead(ConstantPool cp) {
            setDesc(cp.getConstantDesc(super.getUInt("name_index")));
        }
    }
}
