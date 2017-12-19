package org.glavo.editor.classfile.bytecode;


import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.jvm.Opcode;

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
