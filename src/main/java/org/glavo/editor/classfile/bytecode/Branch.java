package org.glavo.editor.classfile.bytecode;


import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.jvm.Opcode;

public class Branch extends Instruction {

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
