/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.control.Label;
import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.file.types.java.classfile.jvm.Opcode;
import org.reactfx.value.Val;

import java.io.IOException;

public class Instruction extends ClassFileComponent {
    public static Instruction readFrom(ClassFileReader reader, int pc) throws IOException {
        int baseOffset = reader.getOffset();

        Opcode opcode = Opcode.valueOf(reader.readUnsignedByte());
        Instruction instruction = new Instruction(opcode, pc);

        switch (opcode) {
            case ldc_w, ldc2_w,
                    getstatic, putstatic,
                    getfield, putfield,
                    invokevirtual, invokespecial, invokestatic,
                    _new,
                    anewarray, checkcast, _instanceof -> {
                assert opcode.operandCount == 2;

                CpIndex<ConstantInfo> operand = instruction.readCpIndexEager(reader, "operand", ConstantInfo.class);
                instruction.descProperty().bind(Val.map(operand.indexProperty(), idx -> new Label(opcode.opName + " #" + idx)));
            }
            case iload, lload, fload, dload, aload, istore, lstore, fstore, dstore, astore -> {
                assert opcode.operandCount == 1;
                U1 operand = instruction.readU1(reader, "operand");
                instruction.descProperty().bind(Val.map(operand.intValueProperty(), v -> new Label(opcode.opName + " " + v)));
            }
            case ifeq, ifne, iflt, ifge, ifgt, ifle,
                    if_icmpeq, if_icmpne, if_icmplt, if_icmpge, if_icmpgt, if_icmple,
                    _goto, ifnull, ifnonnull -> {
                U2 offset = instruction.readU2(reader, "offset");
                instruction.descProperty().bind(Val.map(offset.valueProperty(), off -> new Label(opcode.opName + " " + pc + off)));
            }
            case bipush -> {
                S1 operand = instruction.readS1(reader, "operand");
                instruction.descProperty().bind(Val.map(operand.intValueProperty(), val -> new Label(opcode.opName + " " + val)));
            }
            case sipush -> {
                S2 operand = instruction.readS2(reader, "operand");
                instruction.descProperty().bind(Val.map(operand.intValueProperty(), val -> new Label(opcode.opName + " " + val)));
            }
            case ldc -> {
                CpIndex<ConstantInfo> operand = instruction.readU1CpIndex(reader, "operand", ConstantInfo.class);
                instruction.descProperty().bind(Val.map(operand.indexProperty(), idx -> new Label(opcode.opName + " #" + idx)));
            }
            case iinc -> {
                U1 index = instruction.readU1(reader, "index");
                S1 _const = instruction.readS1(reader, "const");

                instruction.descProperty().bind(Val.combine(index.intValueProperty(), _const.intValueProperty(),
                        (idx, value) -> new Label(opcode.opName + " " + idx + ", " + value)));
            }
            case tableswitch -> {
                final CheckedFunction<ClassFileReader, ClassFileComponent, IOException> readJumpOffset = r -> {
                    ClassFileComponent component = new ClassFileComponent();
                    U4 offset = component.readU4(reader, "offset");
                    component.setDesc(new Label(String.valueOf(offset.getIntValue() + pc)));
                    component.setLength(4);
                    return component;
                };

                int paddingLength = (pc - 1) % 4;
                if (paddingLength != 0)
                    instruction.readBytes(reader, "padding", paddingLength);

                instruction.read(reader, "default", readJumpOffset);

                U4 low = instruction.readU4(reader, "low");
                U4 high = instruction.readU4(reader, "high");

                // high - low + 1 signed 32-bit offsets
                for (int i = low.getIntValue(); i <= high.getIntValue(); i++) {
                    instruction.read(reader, String.valueOf(i), readJumpOffset);
                }

                instruction.setDesc(new Label(opcode.opName));
            }
            case lookupswitch -> {
                int paddingLength = (pc - 1) % 4;
                if (paddingLength != 0)
                    instruction.readBytes(reader, "padding", paddingLength);

                instruction.read(reader, "default", r -> new U4(r.readInt() + pc));

                U4 npairs = instruction.readU4(reader, "npairs");
                for (int i = 0; i < npairs.getIntValue(); i++) {
                    ClassFileComponent matchOffset = new ClassFileComponent();
                    U4 match = matchOffset.readU4(reader, "match");
                    U4 offset = matchOffset.readU4(reader, "offset");

                    matchOffset.setName(String.valueOf(match.getIntValue()));
                    matchOffset.setDesc(new Label(String.valueOf(offset.getIntValue() + pc)));

                    matchOffset.setLength(8);
                    instruction.getChildren().add(matchOffset);
                }

                instruction.setDesc(new Label(opcode.opName));
            }
            case invokeinterface -> {
                CpIndex<ConstantInfo> index = instruction.readCpIndex(reader, "index", ConstantInfo.class);
                U1 count = instruction.readU1(reader, "count");
                instruction.readU1(reader, "zero");

                instruction.descProperty().bind(Val.combine(index.constantInfoProperty(), count.intValueProperty(),
                        (info, cv) -> new Label(opcode.opName + " " + info.getDescText() + " " + cv)));
            }
            case invokedynamic -> {
                CpIndex<ConstantInfo> index = instruction.readCpIndex(reader, "index", ConstantInfo.class);
                instruction.readU2(reader, "zero");
                instruction.descProperty().bind(Val.map(index.constantInfoProperty(), info -> new Label(opcode.opName + " " + info.getDescText())));
            }
            case newarray -> {
                U1 atype = instruction.readU1(reader, "atype");

                instruction.descProperty().bind(Val.map(atype.intValueProperty(), type -> {
                    String s = switch (type.intValue()) {
                        case 4 -> "boolean";
                        case 5 -> "char";
                        case 6 -> "float";
                        case 7 -> "double";
                        case 8 -> "byte";
                        case 9 -> "short";
                        case 10 -> "int";
                        case 11 -> "long";
                        default -> "unknown";
                    };

                    return new Label(opcode.opName + " " + s);
                }));
            }
            case multianewarray -> {
                CpIndex<ConstantInfo> index = instruction.readCpIndex(reader, "index", ConstantInfo.class);
                U1 dimensions = instruction.readU1(reader, "dimensions");

                instruction.descProperty().bind(Val.combine(index.constantInfoProperty(), dimensions.intValueProperty(),
                        (info, dim) -> new Label(opcode.opName + " " + info.getDescText() + " " + dim)));
            }
            case wide -> {
                U1 wideOpcode = instruction.readU1(reader, "wide_opcode");
                if (wideOpcode.getIntValue() == Opcode.iinc.opcode) {
                    instruction.readBytes(reader, "operand", 4);
                } else {
                    instruction.readBytes(reader, "operand", 2);
                }
            }
            default -> {
                if (opcode.operandCount > 0)
                    instruction.readBytes(reader, "operands", opcode.operandCount);
                instruction.setDesc(new Label(opcode.opName));
            }
        }

        instruction.setLength(reader.getOffset() - baseOffset);
        return instruction;
    }

    private final Opcode opcode;
    private final int pc;

    Instruction(Opcode opcode, int pc) {
        this.opcode = opcode;
        this.pc = pc;

        this.getChildren().add(new OpcodeComponent(opcode));
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public int getPC() {
        return pc;
    }

    public static final class OpcodeComponent extends ClassFileComponent {
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
}
