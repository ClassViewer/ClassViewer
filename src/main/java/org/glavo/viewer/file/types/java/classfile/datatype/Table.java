package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.binding.Bindings;
import kala.function.CheckedFunction;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;

import java.io.IOException;

public class Table<C extends ClassFileComponent> extends ClassFileComponent {
    public static <C extends ClassFileComponent> Table<C> readFrom(
            ClassFileReader reader, UInt length, CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        Table<C> table = new Table<>();
        int len = length.getIntValue();
        for (int i = 0; i < len; i++) {
            table.getChildren().add(f.applyChecked(reader));
        }
        length.intValueProperty().bind(Bindings.size(table.getChildren()));

        return table;
    }

    @SuppressWarnings("unchecked")
    public C getEntry(int idx) {
        return (C) getChildren().get(idx);
    }
}
