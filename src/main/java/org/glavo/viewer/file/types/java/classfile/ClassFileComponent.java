package org.glavo.viewer.file.types.java.classfile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import kala.function.CheckedFunction;
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
    private Tooltip tooltip;

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

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends ClassFileComponent> T component(int n) {
        return (T) getChildren().get(n);
    }

    protected <T extends ClassFileComponent> T read(ClassFileReader reader, String name, CheckedFunction<ClassFileReader, T, IOException> func) throws IOException {
        var component = func.apply(reader);
        component.setName(name);
        this.getChildren().add(component);
        return component;
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

    protected Bytes readBytes(ClassFileReader reader, String name, int length) throws IOException {
        var bytes = new Bytes(reader.readNBytes(length));
        bytes.setName(name);
        this.getChildren().add(bytes);
        return bytes;
    }

    protected Bytes readBytes(ClassFileReader reader, String name, UInt length) throws IOException {
        var bytes = new Bytes(reader.readNBytes(length.getIntValue()));
        bytes.setName(name);
        this.getChildren().add(bytes);
        return bytes;
    }

    protected S1 readS1(ClassFileReader reader, String name) throws IOException {
        var sint = new S1(reader.readByte());
        sint.setName(name);
        this.getChildren().add(sint);
        return sint;
    }
    protected S2 readS2(ClassFileReader reader, String name) throws IOException {
        var sint = new S2(reader.readInt());
        sint.setName(name);
        this.getChildren().add(sint);
        return sint;
    }

    protected S4 readS4(ClassFileReader reader, String name) throws IOException {
        var sint = new S4(reader.readInt());
        sint.setName(name);
        this.getChildren().add(sint);
        return sint;
    }

    protected <T extends ConstantInfo> CpIndex<T> readCpIndex(ClassFileReader reader, String name, Class<T> type) throws IOException {
        CpIndex<T> cpIdx = reader.readCpIndex(type);
        cpIdx.setName(name);
        this.getChildren().add(cpIdx);
        return cpIdx;
    }

    protected <T extends ConstantInfo> CpIndex<T> readU1CpIndex(ClassFileReader reader, String name, Class<T> type) throws IOException {
        CpIndex<T> cpIdx = reader.readU1CpIndex(type);
        cpIdx.setName(name);
        this.getChildren().add(cpIdx);
        return cpIdx;
    }


    protected <T extends ConstantInfo> CpIndex<T> readCpIndexEager(ClassFileReader reader, String name, Class<T> type) throws IOException {
        CpIndex<T> cpIdx = reader.readCpIndexEager(type);
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

    protected U1 readU1TableLength(ClassFileReader reader, String name) throws IOException {
        U1 length = reader.readU1TableLength();
        length.setName(name);
        this.getChildren().add(length);
        return length;
    }

    protected U2 readU2TableLength(ClassFileReader reader, String name) throws IOException {
        U2 length = reader.readU2TableLength();
        length.setName(name);
        this.getChildren().add(length);
        return length;
    }

    protected <C extends ClassFileComponent> Table<C> readTable(ClassFileReader reader, String name,
                                                                CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        return readTable(reader, name, f, false);
    }

    protected <C extends ClassFileComponent> Table<C> readTable(ClassFileReader reader, String name,
                                                                CheckedFunction<ClassFileReader, C, IOException> f, boolean showIndex) throws IOException {
        Table<C> table = reader.readTable(f, showIndex);
        table.setName(name);
        this.getChildren().add(table);
        return table;
    }

    protected <C extends ClassFileComponent> Table<C> readTable(ClassFileReader reader, String name,
                                                                UInt length,
                                                                CheckedFunction<ClassFileReader, C, IOException> f) throws IOException {
        return readTable(reader, name, length, f, false);
    }

    protected <C extends ClassFileComponent> Table<C> readTable(ClassFileReader reader, String name,
                                                                UInt length,
                                                                CheckedFunction<ClassFileReader, C, IOException> f, boolean showIndex) throws IOException {
        Table<C> table = Table.readFrom(reader, length, f, showIndex);
        table.setName(name);
        this.getChildren().add(table);
        return table;
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

    public ConstantPool getConstantPool() {
        ClassFileComponent component = this;

        while (component.getParent() != null) {
            component = component.getParent().getValue();
        }

        //assert component instanceof ClassFile;
        return component.getConstantPool();
    }

    public ClassFileTreeView getView() {
        ClassFileComponent component = this;

        while (component.getParent() != null) {
            component = component.getParent().getValue();
        }

        //assert component instanceof ClassFile;
        return component.getView();
    }
}
