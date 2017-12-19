package org.glavo.editor.classfile.datatype;

/**
 * Unsigned four-byte quantity.
 */
public class U4 extends UInt {

    public U4() {
        super(READ_U4, TO_STRING);
    }

}
