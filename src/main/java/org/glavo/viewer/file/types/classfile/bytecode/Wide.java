package org.glavo.viewer.file.types.classfile.bytecode;


import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.jvm.Opcode;

public final class Wide extends Instruction {

    public Wide(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        int wideOpcode = reader.readUnsignedByte();
        if (wideOpcode == Opcode.iinc.opcode) {
            reader.skipBytes(4);
        } else {
            reader.skipBytes(2);
        }
    }
    
}
