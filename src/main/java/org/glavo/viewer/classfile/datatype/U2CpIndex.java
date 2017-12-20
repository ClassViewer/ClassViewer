package org.glavo.viewer.classfile.datatype;

/**
 * Same as U2, but used as index of ConstantPool.
 */
public final class U2CpIndex extends UInt {

    public U2CpIndex() {
        super(READ_U2, TO_CONST);
    }

}
