package org.glavo.viewer.file.types.classfile.bytecode;


import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

public final class Bipush extends Instruction {

    public Bipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        byte operand = reader.readByte();
        setDesc(getDesc() + " " + operand);
    }
    
}
