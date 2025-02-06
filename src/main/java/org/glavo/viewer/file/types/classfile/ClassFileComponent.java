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
package org.glavo.viewer.file.types.classfile;


import org.glavo.viewer.file.types.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.classfile.datatype.*;
import org.glavo.viewer.FileComponent;

import java.util.List;

/**
 * Base class for all class file components.
 */
public abstract class ClassFileComponent extends FileComponent {

    /**
     * Reads content, records offset and length.
     *
     * @param reader
     */
    public final void read(ClassFileReader reader) {
        int offset = reader.getPosition();
        readContent(reader);
        int length = reader.getPosition() - offset;
        super.setOffset(offset);
        super.setLength(length);
    }

    /**
     * Reads content using ClassFileReader.
     *
     * @param reader
     */
    protected void readContent(ClassFileReader reader) {
        for (FileComponent fc : getComponents()) {
            ((ClassFileComponent) fc).read(reader);
        }
    }

    protected void postRead(ConstantPool cp) {

    }

    protected int getUInt(String name) {
        return ((UInt) get(name)).getIntValue();
    }

    protected final void u1(String name) {
        this.add(name, new U1());
    }

    protected final void u1cp(String name) {
        this.add(name, new U1CpIndex());
    }

    protected final void u2(String name) {
        this.add(name, new U2());
    }

    protected final void u2cp(String name) {
        this.add(name, new U2CpIndex());
    }

    protected final void u2af(String name, int afType) {
        this.add(name, new U2AccessFlags(afType));
    }

    protected final void u4(String name) {
        this.add(name, new U4());
    }

    protected final void u4hex(String name) {
        this.add(name, new U4Hex());
    }

    protected final void table(String name,
                               Class<? extends ClassFileComponent> entryClass) {
        UInt length = (UInt) getComponents().get(getComponents().size() - 1);
        Table table = new Table(length, entryClass);
        this.add(name, table);
    }

    protected final void bytes(String name) {
        UInt count = (UInt) getComponents().get(getComponents().size() - 1);
        Bytes bytes = new Bytes(count);
        this.add(name, bytes);
    }

    protected final void add(ClassFileComponent subComponent) {
        this.add(null, subComponent);
    }

    @SuppressWarnings("unchecked")
    public final void walkComponentTree(java.util.function.Consumer<ClassFileComponent> f) {
        f.accept(this);
        for (ClassFileComponent component : (List<ClassFileComponent>) (List) getComponents()) {
            if (component != null) {
                component.walkComponentTree(f);
            }
        }
    }

}
