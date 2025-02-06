package org.glavo.viewer.file.types.classfile.bytecode;


import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

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
