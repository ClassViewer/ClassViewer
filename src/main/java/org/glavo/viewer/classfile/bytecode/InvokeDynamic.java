package org.glavo.viewer.classfile.bytecode;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.jvm.Opcode;

public class InvokeDynamic extends Instruction {

    {
        u1  ("opcode");
        u2cp("index");
        u2  ("zero");
    }

    public InvokeDynamic(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("index").getDesc());
    }
    
}
