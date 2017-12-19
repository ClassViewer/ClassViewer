package org.glavo.editor.classfile.constant;

import org.glavo.editor.classfile.ClassFileComponent;
import org.glavo.editor.classfile.ClassFileReader;
import org.glavo.editor.classfile.datatype.U2;
import org.glavo.editor.classfile.jvm.Mutf8Decoder;
import org.glavo.editor.common.ParseException;
import org.glavo.editor.helper.StringHelper;


import java.io.IOException;
import java.util.Objects;

/*
CONSTANT_Utf8_info {
    u1 tag;
    u2 length;
    u1 bytes[length];
}
*/
public class ConstantUtf8Info extends ConstantInfo {

    {
        U2 length = new U2();

        add("length", length);
        add("bytes", new Mutf8(length));
    }

    public String getString() {
        return ((Mutf8) Objects.requireNonNull(super.get("bytes"))).str;
    }

    @Override
    protected String loadDesc(ConstantPool cp) {
        Mutf8 bytes = (Mutf8) super.get("bytes");
        return StringHelper.cutAndAppendEllipsis(bytes.getDesc(), 100);
    }


    // UTF8 String in constant pool.
    private class Mutf8 extends ClassFileComponent {

        private final U2 length;
        private String str;

        public Mutf8(U2 length) {
            this.length = length;
        }

        @Override
        protected void readContent(ClassFileReader reader) {
            byte[] bytes = reader.readBytes(length.getValue());
            try {
                str = Mutf8Decoder.decodeMutf8(bytes);
            } catch (IOException e) {
                throw new ParseException(e);
            }

            setDesc(str);
        }

    }

}
