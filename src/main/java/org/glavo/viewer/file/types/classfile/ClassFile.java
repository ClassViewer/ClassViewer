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
import org.glavo.viewer.file.types.classfile.datatype.Table;
import org.glavo.viewer.file.types.classfile.datatype.U2;
import org.glavo.viewer.file.types.classfile.datatype.U2AccessFlags;
import org.glavo.viewer.file.types.classfile.datatype.U2CpIndex;
import org.glavo.viewer.file.types.classfile.jvm.AccessFlagType;
import org.glavo.viewer.util.ImageUtils;

import java.util.List;

/*
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
*/
public final class ClassFile extends ClassFileComponent {
    {
        U2 cpCount = new U2();

        u4hex("magic");
        u2("minor_version");
        u2("major_version");
        add("constant_pool_count", cpCount);
        add("constant_pool", new ConstantPool(cpCount));
        u2af("access_flags", AccessFlagType.AF_CLASS);
        u2cp("this_class");
        u2cp("super_class");
        u2("interfaces_count");
        table("interfaces", U2CpIndex.class);
        u2("fields_count");
        table("fields", FieldInfo.class);
        u2("methods_count");
        table("methods", MethodInfo.class);
        u2("attributes_count");
        table("attributes", AttributeInfo.class);

    }

    public ConstantPool getConstantPool() {
        return (ConstantPool) super.get("constant_pool");
    }

    @Override
    protected void postRead(ConstantPool cp) {
        U2AccessFlags acc = (U2AccessFlags) get("access_flags");
        if (acc != null) {

            HBox box = new HBox();
            boolean isKt = isKotlin();
            Node view = null;

            if (acc.isAnnotation()) {
                view = new ImageView(ImageUtils.annotationImage);
            } else if (acc.isEnum()) {
                view = new ImageView(ImageUtils.enumImage);
            } else if (acc.isInterface()) {
                view = new ImageView(ImageUtils.interfaceImage);
            } else if (acc.isAbstract()) {
                view = new ImageView(ImageUtils.abstractClassImage);
            } else {
                view = new ImageView(ImageUtils.classImage);
            }

            if (acc.isFinal()) {
                view = new Group(view, new ImageView(ImageUtils.finalMarkImage));
            }

            if (acc.isStatic()) {
                view = new Group(view, new ImageView(ImageUtils.staticMarkImage));
            }

            if (isRunnable()) {
                view = new Group(view, new ImageView(ImageUtils.runnableMarkImage));
            }

            box.getChildren().add(view);

            if (acc.isPrivate()) {
                box.getChildren().add(new ImageView(ImageUtils.privateImage));
            } else if (acc.isProtected()) {
                box.getChildren().add(new ImageView(ImageUtils.protectedImage));
            } else if (acc.isPublic()) {
                box.getChildren().add(new ImageView(ImageUtils.publicImage));
            } else {
                box.getChildren().add(new ImageView(ImageUtils.plocalImage));
            }

            setGraphic(box);
        }
    }

    private boolean isKotlin() {
        return false; //todo
    }

    @SuppressWarnings("unchecked")
    private boolean isRunnable() {
        Table methods = (Table) get("methods");
        if (methods == null)
            return false;
        for (MethodInfo method : (List<MethodInfo>) (List) methods.getComponents()) {
            if (method != null
                    && "main".equals(method.getDesc())
                    && "([Ljava/lang/String;)V".equals(getConstantPool().getUtf8String(
                    method.getUInt("descriptor_index"))))
                return true;
        }

        return false;
    }


}
