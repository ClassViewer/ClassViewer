package org.glavo.viewer.classfile.bytecode;


import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.jvm.Opcode;

public class Sipush extends Instruction {

    public Sipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        short operand = reader.readShort();
        setDesc(getDesc() + " " + operand);
    }
    
}
