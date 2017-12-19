package org.glavo.editor.classfile.datatype;



import org.glavo.editor.classfile.ClassFileComponent;
import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.constant.ConstantPool;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class UInt extends ClassFileComponent {

    protected static final Function<ClassFileReader, Integer> READ_U1 = ClassFileReader::readUnsignedByte;
    protected static final Function<ClassFileReader, Integer> READ_U2 = ClassFileReader::readUnsignedShort;
    protected static final Function<ClassFileReader, Integer> READ_U4 = ClassFileReader::readInt;

    protected static final BiFunction<Integer, ConstantPool, String> TO_STRING =
            (val, cp) -> val.toString();
    protected static final BiFunction<Integer, ConstantPool, String> TO_HEX =
            (val, cp) -> "0x" + Integer.toHexString(val).toUpperCase();
    protected static final BiFunction<Integer, ConstantPool, String> TO_CONST =
            (val, cp) -> val > 0
                    ? "#" + val + "->" + cp.getConstantDesc(val)
                    : "#" + val;


    private final Function<ClassFileReader, Integer> intReader;
    private final BiFunction<Integer, ConstantPool, String> intDescriber;
    private int value;

    public UInt(Function<ClassFileReader, Integer> intReader,
                BiFunction<Integer, ConstantPool, String> intDescriber) {
        this.intReader = intReader;
        this.intDescriber = intDescriber;
    }

    public final int getValue() {
        return value;
    }

    @Override
    protected final void readContent(ClassFileReader reader) {
        value = intReader.apply(reader);
    }

    @Override
    protected final void postRead(ConstantPool cp) {
        setDesc(intDescriber.apply(value, cp));
    }
    
}
