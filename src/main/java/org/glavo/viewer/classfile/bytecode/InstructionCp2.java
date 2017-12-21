package org.glavo.viewer.classfile.bytecode;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.jvm.Opcode;

/**
 * The instruction whose operand is U2CpIndex.
 */
public final class InstructionCp2 extends Instruction {

    {
        u1  ("opcode");
        u2cp("operand");
    }

    public InstructionCp2(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("operand").getDesc());
    }
    
}
