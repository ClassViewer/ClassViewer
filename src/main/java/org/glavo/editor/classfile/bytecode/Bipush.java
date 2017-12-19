package org.glavo.editor.classfile.bytecode;


import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.jvm.Opcode;

public class Bipush extends Instruction {

    public Bipush(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void readOperands(ClassFileReader reader) {
        byte operand = reader.readByte();
        setDesc(getDesc() + " " + operand);
    }
    
}
