/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java.classfile.datatype;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileTreeView;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.StringUtils;
import org.glavo.viewer.util.TextUtils;
import org.reactfx.value.Val;

public class CpIndex<T extends ConstantInfo> extends ClassFileComponent {
    private final IntegerProperty index = new SimpleIntegerProperty();
    private final Class<T> type;

    private final ObjectProperty<T> constantInfo = new SimpleObjectProperty<>();
    private final ObjectProperty<Hyperlink> infoLink = new SimpleObjectProperty<>();
    private final StringProperty formattedIndex = new SimpleStringProperty();

    public CpIndex(int length, Class<T> type) {
        this.type = type;
        this.setLength(length);
    }

    public CpIndex(int length, Class<T> type, int value) {
        this.type = type;
        this.setLength(length);
        this.setIndex(value);
    }

    public CpIndex(Class<T> type) {
        this(2, type);
    }

    public CpIndex(Class<T> type, int value) {
        this(2, type, value);
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

    public String getFormattedIndex() {
        return formattedIndex.get();
    }

    public StringProperty formattedIndexProperty() {
        return formattedIndex;
    }

    public void setFormattedIndex(String formattedIndex) {
        this.formattedIndex.set(formattedIndex);
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

    private boolean isLoaded = false;

    @Override
    public void loadDesc(ClassFileTreeView view) {
        if (isLoaded) return;
        isLoaded = true;
        updateConstantInfo(view);
    }

    private void updateConstantInfo(ClassFileTreeView view) {
        ConstantPool constantPool = view.getConstantPool();
        formattedIndex.bind(Bindings.createStringBinding(() ->
                StringUtils.formatIndex(getIndex(), constantPool.getConstants().size()), indexProperty()));

        int idx = index.get();
        boolean validIndex = false;

        if (idx == 0) {
            constantInfo.set(null);

            Hyperlink link = TextUtils.createHyperlinkWithoutPadding(getFormattedIndex());
            link.getStyleClass().add("cp-index-hyper-link");
            link.setDisable(true);
            infoLink.set(link);
            this.descProperty().bind(infoLink);
            return;
        }

        if (idx > 0 && idx < constantPool.getConstants().size()) {
            validIndex = true;

            ConstantInfo info = constantPool.getConstants().get(idx);
            if (type.isInstance(info)) {
                constantInfo.set(type.cast(info));

                Hyperlink link = TextUtils.createHyperlinkWithoutPadding(getFormattedIndex());
                link.setOnAction(event -> {
                    view.getSelectionModel().select(getConstantInfo());
                    view.scrollTo(view.getRow(getConstantInfo()));
                });
                link.getStyleClass().add("cp-index-hyper-link");
                infoLink.set(link);

                this.descProperty().bind(Val.flatMap(constantInfoProperty(), ConstantInfo::descTextProperty)
                        .map(text -> {
                            if (text == null) return link;

                            return new TextFlow(link, new Text(" -> "), StringUtils.cutTextNode(text, Text::new));
                        }));
                return;
            }
        }

        constantInfo.set(null);

        Hyperlink link = TextUtils.createHyperlinkWithoutPadding("%s (%s)".formatted(
                StringUtils.formatIndex(idx, constantPool.getConstants().size()),
                I18N.getString(validIndex ? "java.classfile.typeMismatch" : "java.classfile.invalidCpIndex")
        ));
        link.setTextFill(Color.RED);
        link.getStyleClass().add("cp-index-hyper-link");
        infoLink.set(link);
        this.descProperty().bind(infoLink);
    }
}
