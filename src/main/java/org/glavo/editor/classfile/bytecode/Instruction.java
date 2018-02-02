package org.glavo.editor.classfile.bytecode;


import org.glavo.editor.classfile.ClassFileComponent;
import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.jvm.Opcode;

/**
 * Base class for all instructions.
 */
public class Instruction extends ClassFileComponent {

    protected final Opcode opcode;
    protected final int pc;

    public Instruction(Opcode opcode, int pc) {
        this.opcode = opcode;
        this.pc = pc;
        setDesc(opcode.name());
    }

    public int getPc() {
        return pc;
    }
    
    @Override
    protected final void readContent(ClassFileReader reader) {
        if (!super.getComponents().isEmpty()) {
            super.readContent(reader);
        } else {
            reader.readUnsignedByte(); // opcode
            readOperands(reader);
        }
    }
    
    protected void readOperands(ClassFileReader reader) {
        if (opcode.operandCount > 0) {
            reader.skipBytes(opcode.operandCount);
        }
    }

}
