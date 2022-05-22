package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

/*
CONSTANT_Double_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
*/
public final class ConstantDoubleInfo extends ConstantInfo {
    public ConstantDoubleInfo(ConstantInfo.Tag tag, U4 highBytes, U4 lowBytes) {
        super(tag);
        highBytes.setName("high_bytes");
        lowBytes.setName("low_bytes");

        this.getChildren().setAll(tag, highBytes, lowBytes);

        this.descProperty().bind(Bindings.createObjectBinding(() -> {
                    long high = highBytes.getIntValue();
                    long low = lowBytes.getIntValue();
                    return new Label(String.valueOf(Double.longBitsToDouble((high << 32) + low)));
                },
                highBytes.intValueProperty(), lowBytes.intValueProperty()));
    }

    public U4 getHighBytes() {
        return (U4) getChildren().get(1);
    }

    public U4 getLowBytes() {
        return (U4) getChildren().get(2);
    }
}
