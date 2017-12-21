package org.glavo.viewer.classfile.bytecode;

import org.glavo.viewer.classfile.constant.ConstantPool;
import org.glavo.viewer.classfile.jvm.Opcode;

/**
 * The instruction whose operand is U1CpIndex.
 */
public final class InstructionCp1 extends Instruction {

    {
        u1("opcode");
        u1cp("operand");
    }

    public InstructionCp1(Opcode opcode, int pc) {
        super(opcode, pc);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        setDesc(getDesc() + " " + super.get("operand").getDesc());
    }

}
