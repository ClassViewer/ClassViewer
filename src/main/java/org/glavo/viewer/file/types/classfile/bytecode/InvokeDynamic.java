package org.glavo.viewer.file.types.classfile.bytecode;

import org.glavo.viewer.file.types.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

public final class InvokeDynamic extends Instruction {

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
