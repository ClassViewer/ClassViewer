package org.glavo.viewer.file.types.java.classfile;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.HexPane;

public class ClassFileTreeView extends TreeView<ClassFileComponent> {

    public ClassFileTreeView(FileTab tab) {
        this.setCellFactory(view -> new Cell());

        this.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                if (tab.getContent() instanceof HexPane hexPane) {
                    hexPane.select(newValue.getValue().getOffset(), newValue.getValue().getLength());
                }
            }
        });
    }

    public ClassFile getClassFile() {
        return (ClassFile) getRoot().getValue();
    }

    public ConstantPool getConstantPool() {
        return getClassFile().getConstantPool();
    }

    public static final class Cell extends TreeCell<ClassFileComponent> {
        @Override
        protected void updateItem(ClassFileComponent item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox box = new HBox();
                Node icon = item.getIcon();
                String name = item.getName();
                Node desc = item.getDesc();

                if (icon != null) box.getChildren().add(icon);
                if (name != null) box.getChildren().add(new Label(desc == null ? name : name + ":"));
                if (desc != null) box.getChildren().add(desc);
                setText(null);
                setGraphic(box);
            }
        }
    }
}
