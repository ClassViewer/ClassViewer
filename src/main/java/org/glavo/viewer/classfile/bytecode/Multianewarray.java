package org.glavo.viewer.classfile.bytecode;


import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.jvm.Opcode;

public final class Multianewarray extends Instruction {

    {
        u1  ("opcode");
        u2cp("index");
        u1  ("dimensions");
    }

    public Multianewarray(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        setDesc(getDesc() + " "
                + super.get("index").getDesc() + ", "
                + super.getUInt("dimensions"));
    }
    
}
