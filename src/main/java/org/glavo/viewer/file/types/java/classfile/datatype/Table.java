package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.binding.Bindings;
import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.util.StringUtils;

import java.io.IOException;

public class Table<C extends ClassFileComponent> extends ClassFileComponent {
    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        return readFrom(reader, length, f, false);
    }

    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> f, boolean showIndex) throws IOException {
        int offset = reader.getOffset();

        Table<C> table = new Table<>();
        int len = length.getIntValue();
        for (int i = 0; i < len; i++) {
            C c = f.applyChecked(reader);
            if (showIndex) {
                c.setName(StringUtils.formatIndex(i, len));
            }
            table.getChildren().add(c);
        }
        length.intValueProperty().bind(Bindings.size(table.getChildren()));

        table.setLength(reader.getOffset() - offset);
        return table;
    }

    @SuppressWarnings("unchecked")
    public C getEntry(int idx) {
        return (C) getChildren().get(idx);
    }
}
