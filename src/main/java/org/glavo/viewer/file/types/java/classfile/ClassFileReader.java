package org.glavo.viewer.file.types.java.classfile;

import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.*;
import org.glavo.viewer.file.types.java.classfile.jvm.Mutf8Decoder;

import java.io.*;

public class ClassFileReader {
    private int offset = 0;
    private DataInputStream input;

    ClassFile classFile;

    public ClassFileReader(InputStream input) {
        this.input = new DataInputStream(input);
    }

    public int getOffset() {
        return offset;
    }

    public ClassFile getClassFile() {
        return classFile;
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
        byte[] res = input.readNBytes(n);
        if (res.length != n) throw new EOFException();
        return res;
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

    public S1 readS1() throws IOException {
        return new S1(readByte());
    }

    public S2 readS2() throws IOException {
        return new S2(readShort());
    }

    public S4 readS4() throws IOException {
        return new S4(readInt());
    }

    private UInt tableLength;

    public U1 readU1TableLength() throws IOException {
        if (tableLength != null) throw new AssertionError("tableLength = " + tableLength);

        tableLength = readU1();
        return (U1) tableLength;
    }

    public U2 readU2TableLength() throws IOException {
        if (tableLength != null) throw new AssertionError("tableLength = " + tableLength);

        tableLength = readU2();
        return (U2) tableLength;
    }

    public <C extends ClassFileComponent> Table<C> readTable(CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        return readTable(f, false);
    }

    public <C extends ClassFileComponent> Table<C> readTable(CheckedFunction<ClassFileReader, C, IOException> f, boolean showIndex) throws IOException {
        if (tableLength == null) throw new AssertionError("tableLength = null");

        UInt len = tableLength;
        tableLength = null;

        return Table.readFrom(this, len, f, showIndex);
    }

    public <T extends ConstantInfo> CpIndex<T> readCpIndex(Class<T> type) throws IOException {
        return new CpIndex<>(type, readUnsignedShort());
    }

    public <T extends ConstantInfo> CpIndex<T> readU1CpIndex(Class<T> type) throws IOException {
        return new CpIndex<>(1, type, readUnsignedByte());
    }

    public <T extends ConstantInfo> CpIndex<T> readCpIndexEager(Class<T> type) throws IOException {
        CpIndex<T> idx = readCpIndex(type);
        idx.loadDesc(getClassFile().getView());
        return idx;
    }


}
