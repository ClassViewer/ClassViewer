package org.glavo.viewer.file.types.classfile.bytecode;


import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

public final class Sipush extends Instruction {

    public Sipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        short operand = reader.readShort();
        setDesc(getDesc() + " " + operand);
    }
    
}
