/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.file.types.classfile;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.glavo.viewer.file.types.classfile.attribute.AttributeInfo;
import org.glavo.viewer.file.types.classfile.constant.ConstantPool;
import org.glavo.viewer.file.types.classfile.datatype.U2AccessFlags;
import org.glavo.viewer.file.types.classfile.jvm.AccessFlagType;

/*
method_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
 */
public class MethodInfo extends ClassFileComponent {

    {
        u2af("access_flags", AccessFlagType.AF_METHOD);
        u2cp("name_index");
        u2cp("descriptor_index");
        u2("attributes_count");
        table("attributes", AttributeInfo.class);
    }

    @Override
    protected void postRead(ConstantPool cp) {
        int nameIndex = super.getUInt("name_index");
        if (nameIndex > 0) {
            // todo fix loading java.lang.String from rt.jar
            setDesc(cp.getUtf8String(nameIndex));
        }
        U2AccessFlags acc = (U2AccessFlags) get("access_flags");

        if (acc != null) {
            HBox box = new HBox();
            Node view;

            if (acc.isAbstract()) {
                view = new ImageView(ClassFile.ICON_ABSTRACT_METHOD);
            } else if (("<init>".equals(this.getDesc()) || "<clinit>".equals(this.getDesc()))) {
                view = new ImageView(ClassFile.ICON_CLASS_INITIALIZER);
            } else {
                view = new ImageView(ClassFile.ICON_METHOD);
            }

            if (acc.isFinal()) {
                view = new Group(view, new ImageView(ClassFile.ICON_FINAL_MARK));
            } else if (acc.isStatic()) {
                view = new Group(view, new ImageView(ClassFile.ICON_STATIC_MARK));
            }

            box.getChildren().add(view);

            if (acc.isPrivate()) {
                box.getChildren().add(new ImageView(ClassFile.ICON_ACC_PRIVATE));
            } else if (acc.isProtected()) {
                box.getChildren().add(new ImageView(ClassFile.ICON_ACC_PROTECTED));
            } else if (acc.isPublic()) {
                box.getChildren().add(new ImageView(ClassFile.ICON_ACC_PUBLIC));
            } else {
                box.getChildren().add(new ImageView(ClassFile.ICON_ACC_PLOCAL));
            }

            setGraphic(box);
        }
    }

}
