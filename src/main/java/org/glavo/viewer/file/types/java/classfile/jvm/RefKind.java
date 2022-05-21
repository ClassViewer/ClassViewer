package org.glavo.viewer.file.types.java.classfile.jvm;

import org.jetbrains.annotations.Nullable;

// http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.5
//@formatter:off
public enum RefKind {

    REF_getField        (1),
    REF_getStatic       (2),
    REF_putField        (3),
    REF_putStatic       (4),
    REF_invokeVirtual   (5),
    REF_invokeStatic    (6),
    REF_invokeSpecial   (7),
    REF_newInvokeSpecial(8),
    REF_invokeInterface (9),
    ;

    private static final RefKind[] values = values();

    public final int kind;

    RefKind(int kind) {
        this.kind = kind;
    }

    public static @Nullable RefKind valueOf(int kind) {
        for (RefKind value : values) {
            if (value.kind == kind) {
                return value;
            }
        }

        return null;
    }

}
