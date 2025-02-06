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

import org.glavo.viewer.file.types.classfile.jvm.AccessFlags;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class U2AccessFlags extends UInt {

    public U2AccessFlags(int afType) {
        super(READ_U2, (val, cp) -> describe(afType, val));
    }

    private static String describe(int flagsType, int flagsVal) {
        return Stream.of(AccessFlags.values())
                .filter(flag -> (flag.type & flagsType) != 0)
                .filter(flag -> (flag.flag & flagsVal) != 0)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public boolean isInterface() {
        return (getIntValue() & AccessFlags.ACC_INTERFACE.flag)!= 0;
    }

    public boolean isEnum() {
        return (getIntValue() & AccessFlags.ACC_ENUM.flag)!= 0;
    }

    public boolean isAbstract() {
        return (getIntValue() & AccessFlags.ACC_ABSTRACT.flag)!= 0;
    }

    public boolean isAnnotation() {
        return (getIntValue() & AccessFlags.ACC_ANNOTATION.flag)!= 0;
    }

    public boolean isStatic() {
        return (getIntValue() & AccessFlags.ACC_STATIC.flag)!= 0;
    }

    public boolean isFinal() {
        return (getIntValue() & AccessFlags.ACC_FINAL.flag)!= 0;
    }

    public boolean isPrivate() {
        return (getIntValue() & AccessFlags.ACC_PRIVATE.flag)!= 0;
    }

    public boolean isProtected() {
        return (getIntValue() & AccessFlags.ACC_PROTECTED.flag)!= 0;
    }

    public boolean isPublic() {
        return (getIntValue() & AccessFlags.ACC_PUBLIC.flag)!= 0;
    }
}
