package org.glavo.viewer.classfile.datatype;



import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.ClassFileReader;
import org.glavo.viewer.classfile.constant.ConstantPool;

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
    private int intValue;

    public UInt(Function<ClassFileReader, Integer> intReader,
                BiFunction<Integer, ConstantPool, String> intDescriber) {
        this.intReader = intReader;
        this.intDescriber = intDescriber;
    }

    public final int getIntValue() {
        return intValue;
    }

    @Override
    protected final void readContent(ClassFileReader reader) {
        intValue = intReader.apply(reader);
    }

    @Override
    protected final void postRead(ConstantPool cp) {
        setDesc(intDescriber.apply(intValue, cp));
    }
    
}
