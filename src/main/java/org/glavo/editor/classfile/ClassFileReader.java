package org.glavo.editor.classfile;

import org.glavo.editor.classfile.constant.ConstantPool;
import org.glavo.editor.common.BytesReader;

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
