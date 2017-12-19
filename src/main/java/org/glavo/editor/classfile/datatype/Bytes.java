package org.glavo.editor.classfile.datatype;


import org.glavo.editor.classfile.ClassFileComponent;
import org.glavo.editor.classfile.ClassFileReader;

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
