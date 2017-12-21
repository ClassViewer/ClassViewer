package org.glavo.viewer.classfile.bytecode;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.jvm.Opcode;

public final class InvokeInterface extends Instruction {

    {
        u1  ("opcode");
        u2cp("index");
        u1  ("count");
        u1  ("zero");
    }

    public InvokeInterface(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " "
                + super.get("index").getDesc() + ", "
                + super.getUInt("count"));
    }
    
}
