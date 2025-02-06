package org.glavo.viewer.file.types.classfile.bytecode;

import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

public final class Iinc extends Instruction {

    public Iinc(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        int index = reader.readUnsignedByte();
        int _const = reader.readByte();
        setDesc(getDesc() + " " + index + ", " + _const);
    }
    
}
