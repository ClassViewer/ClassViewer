package org.glavo.viewer.classfile.datatype;


import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.ClassFileReader;

/**
 * Unparsed bytes.
 */
public class Bytes extends ClassFileComponent {

    private UInt count;

    public Bytes(UInt count) {
        this.count = count;
    }

    @Override
    protected void readContent(ClassFileReader reader) {
        reader.skipBytes(count.getIntValue());
    }

}
