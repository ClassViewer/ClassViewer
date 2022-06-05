package org.glavo.viewer.file.types.java.classfile.bytecode;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.jvm.Opcode;

public final class OpcodeComponent extends ClassFileComponent {
    private final Opcode opcode;

    public OpcodeComponent(Opcode opcode) {
        this.opcode = opcode;
        this.setLength(1);

        this.setName("opcode");
        this.setDesc(new Label(opcode.opDesc));
    }

    public Opcode getOpcode() {
        return opcode;
    }
}
