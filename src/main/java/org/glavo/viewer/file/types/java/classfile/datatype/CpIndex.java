package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.paint.Color;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.StringUtils;
import org.glavo.viewer.util.TextUtils;

public class CpIndex<T extends ConstantInfo> extends ClassFileComponent {
    private final IntegerProperty index = new SimpleIntegerProperty();
    private final Class<T> type;

    private final ObjectProperty<T> constantInfo = new SimpleObjectProperty<>();

    private final ObjectProperty<Hyperlink> infoLink = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> valuePresenter = new SimpleObjectProperty<>();

    public CpIndex(Class<T> type) {
        this.type = type;
        this.setLength(2);

        this.descProperty().bind(infoLink); // TODO
    }

    public CpIndex(Class<T> type, int value) {
        this(type);
        this.setIndex(value);
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public int getIndex() {
        return index.get();
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public Class<T> getType() {
        return type;
    }

    public ReadOnlyObjectProperty<T> constantInfoProperty() {
        return constantInfo;
    }

    public ConstantInfo getConstantInfo() {
        return constantInfo.get();
    }

    @Override
    public String contentToString() {
        return String.valueOf(getIndex());
    }

    @Override
    public void loadDesc(ClassFileTreeView view) {
        updateConstantInfo(view);
    }

    private void updateConstantInfo(ClassFileTreeView view) {
        ConstantPool constantPool = view.getConstantPool();

        int idx = index.get();
        boolean validIndex = false;

        if (idx > 0 && idx < constantPool.getConstants().size()) {
            validIndex = true;

            ConstantInfo info = constantPool.getConstants().get(idx);
            if (type.isInstance(info)) {
                constantInfo.set(type.cast(info));
                valuePresenter.unbind();
                valuePresenter.bind(info.descProperty());

                Hyperlink link = TextUtils.createHyperlinkWithoutPadding(StringUtils.formatIndex(idx, constantPool.getConstants().size()));
                link.setOnAction(event -> {
                    view.getSelectionModel().select(getConstantInfo());
                    view.scrollTo(view.getRow(getConstantInfo()));
                });
                link.getStyleClass().add("cp-index-hyper-link");
                infoLink.set(link);

                return;
            }
        }

        constantInfo.set(null);
        valuePresenter.unbind();
        valuePresenter.set(null);

        Hyperlink link = TextUtils.createHyperlinkWithoutPadding("%s (%s)".formatted(
                StringUtils.formatIndex(idx, constantPool.getConstants().size()),
                I18N.getString(validIndex ? "java.classfile.typeMismatch" : "java.classfile.invalidCpIndex")
        ));
        link.setTextFill(Color.RED);
        link.getStyleClass().add("cp-index-hyper-link");
        infoLink.set(link);
    }
}
