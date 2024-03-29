package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.value.ObservableValue;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.reactfx.value.Val;

/*
CONSTANT_Long_info {
    u1 tag;
    u4 high_bytes;
    u4 low_bytes;
}
*/
public final class ConstantLongInfo extends ConstantValueInfo {
    public ConstantLongInfo(ConstantInfo.Tag tag, U4 highBytes, U4 lowBytes) {
        super(tag);
        highBytes.setName("high_bytes");
        lowBytes.setName("low_bytes");

        this.getChildren().setAll(tag, highBytes, lowBytes);
    }

    public U4 highBytes() {
        return component(1);
    }

    public U4 lowBytes() {
        return component(2);
    }

    @Override
    protected ObservableValue<String> initDescText() {
        return Val.combine(highBytes().intValueProperty(), lowBytes().intValueProperty(),
                (high, low) -> String.valueOf(((high.longValue()) << 32) + low.longValue()));
    }
}
