package org.glavo.viewer.classfile.bytecode;


import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.jvm.Opcode;

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
