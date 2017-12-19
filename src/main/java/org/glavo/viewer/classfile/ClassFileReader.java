package org.glavo.viewer.classfile;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.BytesReader;

import java.nio.ByteOrder;

public class ClassFileReader extends BytesReader {

    private ConstantPool constantPool;

    public ClassFileReader(byte[] data) {
        super(data, ByteOrder.BIG_ENDIAN);
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

}
