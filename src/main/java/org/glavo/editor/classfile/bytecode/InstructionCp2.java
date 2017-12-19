package org.glavo.editor.classfile.bytecode;

import org.glavo.editor.classfile.constant.ConstantPool;
import org.glavo.editor.classfile.jvm.Opcode;

/**
 * The instruction whose operand is U2CpIndex.
 */
public class InstructionCp2 extends Instruction {

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
