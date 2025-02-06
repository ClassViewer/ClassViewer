package org.glavo.viewer.file.types.classfile.bytecode;


import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

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
