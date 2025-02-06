/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.file.types.classfile.datatype;

import org.glavo.viewer.file.types.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.classfile.ClassFileReader;
import org.glavo.viewer.file.types.classfile.constant.ConstantPool;

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
