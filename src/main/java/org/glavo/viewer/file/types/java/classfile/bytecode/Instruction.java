package org.glavo.viewer.file.types.java.classfile.bytecode;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.jvm.Opcode;
import org.reactfx.value.Val;

import java.io.IOException;

public class Instruction extends ClassFileComponent {
    public static Instruction readFrom(ClassFileReader reader, int pc) throws IOException {
        Opcode opcode = Opcode.valueOf(reader.readUnsignedByte());

        Instruction instruction = new Instruction(opcode, pc);

        switch (opcode) {
            case ldc_w, ldc2_w,
                    getstatic, putstatic,
                    getfield, putfield,
                    invokevirtual, invokespecial, invokestatic,
                    _new,
                    anewarray, checkcast, _instanceof -> {
                CpIndex<ConstantInfo> operand = instruction.readCpIndexEager(reader, "operand", ConstantInfo.class);
                instruction.descProperty().bind(Val.map(operand.indexProperty(), idx -> new Label(opcode.opName + " #" + idx)));
            }

        }

        throw new UnsupportedOperationException(); // TODO
    }

    private final Opcode opcode;
    private final int pc;

    Instruction(Opcode opcode, int pc) {
        this.opcode = opcode;
        this.pc = pc;

        this.setName(String.valueOf(pc));
        this.getChildren().add(new OpcodeComponent(opcode));
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public int getPC() {
        return pc;
    }

    @Override
    public boolean isLeaf() {
        return this.opcode.operandCount == 0;
    }
}
