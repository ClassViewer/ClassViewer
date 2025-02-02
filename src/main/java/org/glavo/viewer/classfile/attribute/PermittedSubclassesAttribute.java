package org.glavo.viewer.classfile.attribute;

import org.glavo.viewer.classfile.datatype.U2CpIndex;

public final class PermittedSubclassesAttribute extends AttributeInfo {
    {
        u2   ("number_of_classes");
        table("classes", U2CpIndex.class);
    }
}
