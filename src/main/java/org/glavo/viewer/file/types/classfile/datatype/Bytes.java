package org.glavo.viewer.file.types.classfile.datatype;


import org.glavo.viewer.file.types.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.classfile.ClassFileReader;

/**
 * Unparsed bytes.
 */
public final class Bytes extends ClassFileComponent {

    private UInt count;

    public Bytes(UInt count) {
        this.count = count;
    }

    @Override
    protected void readContent(ClassFileReader reader) {
        reader.skipBytes(count.getIntValue());
    }

}
