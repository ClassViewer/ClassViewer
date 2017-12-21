package org.glavo.viewer.classfile.bytecode;


import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.jvm.Opcode;

public final class Branch extends Instruction {

    public Branch(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        short offset = reader.readShort();
        int jmpTo = pc + offset;
        setDesc(getDesc() + " " + jmpTo);
    }
    
}
