package org.glavo.viewer.file.types.classfile.attribute;

import org.glavo.viewer.file.types.classfile.datatype.U2CpIndex;

public class NestMembersAttribute extends AttributeInfo {
    {
        u2   ("number_of_classes");
        table("classes", U2CpIndex.class);
    }
}
