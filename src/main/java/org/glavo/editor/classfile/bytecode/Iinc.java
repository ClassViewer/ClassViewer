package org.glavo.editor.classfile.bytecode;

import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.jvm.Opcode;

public class Iinc extends Instruction {

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
