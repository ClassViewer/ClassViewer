package org.glavo.viewer.file.types.java.classfile;

import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.file.types.java.classfile.jvm.Mutf8Decoder;

import java.io.*;

public class ClassFileReader {
    private int offset = 0;
    private DataInputStream input;

    public ClassFileReader(InputStream input) {
        this.input = new DataInputStream(input);
    }

    public int getOffset() {
        return offset;
    }

    public byte readByte() throws IOException {
        offset += 1;
        return input.readByte();
    }

    public int readUnsignedByte() throws IOException {
        offset += 1;
        return input.readUnsignedByte();
    }

    public short readShort() throws IOException {
        offset += 2;
        return input.readShort();
    }

    public int readUnsignedShort() throws IOException {
        offset += 2;
        return input.readUnsignedShort();
    }

    public char readChar() throws IOException {
        offset += 2;
        return input.readChar();
    }

    public int readInt() throws IOException {
        offset += 4;
        return input.readInt();
    }

    public long readLong() throws IOException {
        offset += 8;
        return input.readLong();
    }

    public float readFloat() throws IOException {
        offset += 4;
        return input.readFloat();
    }

    public double readDouble() throws IOException {
        offset += 8;
        return input.readDouble();
    }

    public byte[] readNBytes(int n) throws IOException {
        offset += n;
        return input.readNBytes(n);
    }

    public String readUTF() throws IOException {
        int length = input.readUnsignedByte();
        byte[] bytes = input.readNBytes(length);
        if (bytes.length != length) throw new EOFException();

        offset += 2 + length;

        return Mutf8Decoder.decodeMutf8(bytes);
    }

    public U1 readU1() throws IOException {
        return new U1(readUnsignedByte());
    }

    public U2 readU2() throws IOException {
        return new U2(readUnsignedShort());
    }

    public U4 readU4() throws IOException {
        return new U4(readInt());
    }

    public U1Hex readU1Hex() throws IOException {
        return new U1Hex(readUnsignedByte());
    }

    public U2Hex readU2Hex() throws IOException {
        return new U2Hex(readUnsignedShort());
    }

    public U4Hex readU4Hex() throws IOException {
        return new U4Hex(readInt());
    }

    public <T extends ConstantInfo> CpIndex<T> readCpIndex(Class<T> type) throws IOException {
        return new CpIndex<>(type, readUnsignedShort());
    }
}
