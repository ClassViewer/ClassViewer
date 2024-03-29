package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;

public abstract class IntComponent extends ClassFileComponent {
    private final IntegerProperty intValue = new SimpleIntegerProperty();

    protected IntComponent(int length, int value) {
        this.setLength(length);
        this.setIntValue(value);
        this.descProperty().bind(Bindings.createObjectBinding(() -> new Label(contentToString()), intValueProperty()));
    }

    public IntegerProperty intValueProperty() {
        return intValue;
    }

    public int getIntValue() {
        return intValue.get();
    }

    public void setIntValue(int intValue) {
        this.intValue.set(intValue);
    }

    @Override
    public abstract String contentToString();
}
