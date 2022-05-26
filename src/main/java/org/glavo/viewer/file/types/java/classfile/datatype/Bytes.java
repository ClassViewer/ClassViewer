package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;

import java.util.Arrays;

public class Bytes extends ClassFileComponent {
    private final ObjectProperty<byte[]> values = new SimpleObjectProperty<>();

    public Bytes(byte[] values) {
        this.valuesProperty().addListener((o, oldValue, newValue) -> setLength(newValue.length));
        this.setValues(values);
    }

    public ObjectProperty<byte[]> valuesProperty() {
        return values;
    }

    public byte[] getValues() {
        return values.getValue();
    }

    public void setValues(byte[] values) {
        this.values.set(values);
    }

    @Override
    public String contentToString() {
        return Arrays.toString(values.getValue());
    }
}
