package org.glavo.viewer.classfile.bytecode;


import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.jvm.Opcode;

/*
    tableswitch
    <0-3 byte pad>
    defaultbyte1
    defaultbyte2
    defaultbyte3
    defaultbyte4
    lowbyte1
    lowbyte2
    lowbyte3
    lowbyte4
    highbyte1
    highbyte2
    highbyte3
    highbyte4
    jump offsets...
 */
public final class TableSwitch extends Instruction {

    //private final List<JumpOffset> jumpOffsets = new ArrayList<>();
    
    public TableSwitch(Opcode opcode, int pc) {
        super(opcode, pc);
    }
    
    @Override
    protected void readOperands(ClassFileReader reader) {
        skipPadding(reader);
        
        JumpOffset defaultOffset = readJumpOffset(reader, "default");
        
        int low = reader.readInt();
        int high = reader.readInt();
        
        // high - low + 1 signed 32-bit offsets
        for (int i = low; i <= high; i++) {
            JumpOffset offset = readJumpOffset(reader, String.valueOf(i));
            add(offset);
        }

        add(defaultOffset);
    }
    
    private void skipPadding(ClassFileReader reader) {
        for (int i = 1; (pc + i) %4 != 0; i++) {
            reader.readByte();
        }
    }
    
    private JumpOffset readJumpOffset(ClassFileReader reader, String name) {
        JumpOffset offset = new JumpOffset();
        offset.read(reader);
        offset.setName(name);
        offset.setDesc(Integer.toString(pc + offset.offset));
        return offset;
    }

    
    public static class JumpOffset extends ClassFileComponent {

        private int offset;
        
        @Override
        protected void readContent(ClassFileReader reader) {
            offset = reader.readInt();
        }
        
    }
    
}
