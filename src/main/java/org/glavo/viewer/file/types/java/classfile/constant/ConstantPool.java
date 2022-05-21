package org.glavo.viewer.file.types.java.classfile.constant;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.datatype.U2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ConstantPool extends ClassFileComponent {


    private static String formatIndex(int maxIndex, int index) {
        int idxWidth = String.valueOf(maxIndex).length();
        String fmtStr = "#%0" + idxWidth + "d";
        return String.format(fmtStr, index);
    }

    // like #32: (Utf8)
    private static void setConstantName(ConstantInfo constant, int cpCount, int idx) {
        String idxStr = formatIndex(cpCount, idx);
        String constantName = constant.getClass().getSimpleName()
                .replace("Constant", "")
                .replace("Info", "");
        constant.setName(idxStr + " (" + constantName + ")");
    }

    public static ConstantPool readFrom(ClassFileReader reader, U2 cpCount) throws IOException {
        int count = cpCount.getIntValue();
        ObservableList<ConstantInfo> constants = FXCollections.observableList(new ArrayList<>(count));
        constants.add(null);

        // The constant_pool table is indexed from 1 to constant_pool_count - 1
        for (int i = 1; i < count; i++) {
            ConstantInfo c = ConstantInfo.readFrom(reader);
            setConstantName(c, count, i);
            constants.add(c);
            // http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4.5
            // All 8-byte constants take up two entries in the constant_pool table of the class file.
            // If a CONSTANT_Long_info or CONSTANT_Double_info structure is the item in the constant_pool
            // table at index n, then the next usable item in the pool is located at index n+2.
            // The constant_pool index n+1 must be valid but is considered unusable.
            if (c instanceof ConstantLongInfo || c instanceof ConstantDoubleInfo) {
                constants.add(null);
                i++;
            }
        }

        return new ConstantPool(constants);
    }


    private final ObservableList<ConstantInfo> constants;

    private ConstantPool(ObservableList<ConstantInfo> constants) {
        this.constants = constants;
        this.setName("constant_pool");
        Bindings.bindContent(this.getChildren(), new FilteredList<>(constants, Objects::nonNull));
    }

    public ObservableList<ConstantInfo> getConstants() {
        return constants;
    }
}
