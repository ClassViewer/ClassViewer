package org.glavo.viewer.file.types.java.classfile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import kala.value.primitive.IntRef;
import org.glavo.viewer.file.FileComponent;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.java.classfile.datatype.*;

import java.io.IOException;

public class ClassFileComponent extends FileComponent<ClassFileComponent> {
    private final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Node> desc = new SimpleObjectProperty<>();


    public ObjectProperty<Node> iconProperty() {
        return icon;
    }

    public Node getIcon() {
        return icon.get();
    }

    public void setIcon(Node icon) {
        this.icon.set(icon);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObjectProperty<Node> descProperty() {
        return desc;
    }

    public Node getDesc() {
        return desc.get();
    }

    public void setDesc(Node desc) {
        this.desc.set(desc);
    }

    protected U1 readU1(ClassFileReader reader, String name) throws IOException {
        var uint = new U1(reader.readUnsignedByte());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected U2 readU2(ClassFileReader reader, String name) throws IOException {
        var uint = new U2(reader.readUnsignedShort());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected U4 readU4(ClassFileReader reader, String name) throws IOException {
        var uint = new U4(reader.readInt());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected U1Hex readU1Hex(ClassFileReader reader, String name) throws IOException {
        var uint = new U1Hex(reader.readUnsignedByte());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected U2Hex readU2Hex(ClassFileReader reader, String name) throws IOException {
        var uint = new U2Hex(reader.readUnsignedShort());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected U4Hex readU4Hex(ClassFileReader reader, String name) throws IOException {
        var uint = new U4Hex(reader.readInt());
        uint.setName(name);
        this.getChildren().add(uint);
        return uint;
    }

    protected <T extends ConstantInfo> CpIndex<T> readCpIndex(ClassFileReader reader, String name, Class<T> type) throws IOException {
        CpIndex<T> cpIdx = reader.readCpIndex(type);
        cpIdx.setName(name);
        this.getChildren().add(cpIdx);
        return cpIdx;
    }

    protected AccessFlags readAccessFlags(ClassFileReader reader, String name, int flagType) throws IOException {
        var flags = new AccessFlags(flagType, reader.readUnsignedShort());
        flags.setName(name);
        this.getChildren().add(flags);
        return flags;
    }

    public void calculateOffset(IntRef offset) {
        this.setOffset(offset.value);
        if (getChildren().isEmpty()) {
            offset.value += this.getLength();
        } else {
            this.getChildren().forEach(it -> it.getValue().calculateOffset(offset));
        }
    }

    public void loadDesc(ClassFileTreeView view) {
    }
}
