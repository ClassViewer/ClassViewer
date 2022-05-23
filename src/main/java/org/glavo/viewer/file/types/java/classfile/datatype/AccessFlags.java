package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import kala.collection.base.Iterators;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlag;
import org.reactfx.value.Val;

import java.util.EnumSet;
import java.util.Set;

public class AccessFlags extends ClassFileComponent {
    private static final AccessFlag[] allFlags = AccessFlag.values();

    private final int flagType;
    private final IntegerProperty intValue = new SimpleIntegerProperty();

    private final ObservableValue<Set<AccessFlag>> flags;

    public AccessFlags(int flagType, int value) {
        this.flagType = flagType;
        this.setLength(2);
        this.setIntValue(value);

        this.flags = Val.map(intValueProperty(), flagValue -> {
            EnumSet<AccessFlag> res = EnumSet.noneOf(AccessFlag.class);

            for (AccessFlag flag : allFlags) {
                if ((flag.type & flagType) != 0 && (flag.flag & flagValue.intValue()) != 0) {
                    res.add(flag);
                }
            }

            return res;
        });
        this.descProperty().bind(Val.map(flags, fs -> new Label(Iterators.joinToString(fs.iterator(), " | "))));
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

    public int getFlagType() {
        return flagType;
    }

    public Set<AccessFlag> getFlags() {
        return flags.getValue();
    }

    public ObservableValue<Set<AccessFlag>> flagsProperty() {
        return flags;
    }
}
