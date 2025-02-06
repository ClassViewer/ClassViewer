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
package org.glavo.viewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BytesReader {
    
    private final ByteBuffer buf;

    public BytesReader(byte[] data, ByteOrder order) {
        this.buf = ByteBuffer.wrap(data)
                .asReadOnlyBuffer()
                .order(order);
    }

    public int getPosition() {
        return buf.position();
    }

    public byte getByte(int index) {
        return buf.get(index);
    }

    public short getShort(int index) {
        return buf.getShort(index);
    }

    // 8-bit signed int
    public byte readByte() {
        return buf.get();
    }
    
    // 8-bit unsigned int
    public int readUnsignedByte() {
        return Byte.toUnsignedInt(buf.get());
    }
    
    // 16-bit signed int
    public short readShort() {
        return buf.getShort();
    }
    
    // 16-bit unsigned int
    public int readUnsignedShort() {
        return Short.toUnsignedInt(buf.getShort());
    }
    
    // 32-bit signed int
    public int readInt() {
        return buf.getInt();
    }

    // 32-bit unsigned int
    public long readUnsignedInt() {
        return Integer.toUnsignedLong(buf.getInt());
    }

    // 64-bit signed int
    public long readLong() {
        return buf.getLong();
    }

    public float readFloat() {
        return buf.getFloat();
    }

    public double readDouble() {
        return buf.getDouble();
    }

    // byte[]
    public byte[] readBytes(int n) {
        byte[] bytes = new byte[n];
        buf.get(bytes);
        return bytes;
    }

    public void skipBytes(int n) {
        for (int i = 0; i < n; i++) {
            buf.get();
        }
    }

}
