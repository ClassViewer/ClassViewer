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
package org.glavo.viewer.file.types.java.classfile.attribute;

import javafx.scene.control.Label;
import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantModuleInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPackageInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlag;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;
import org.glavo.viewer.util.StringUtils;
import org.reactfx.value.Val;

import java.io.IOException;

/*
Module_attribute {
    u2 attribute_name_index;
    u4 attribute_length;

    u2 module_name_index;
    u2 module_flags;
    u2 module_version_index;

    u2 requires_count;
    {   u2 requires_index;
        u2 requires_flags;
        u2 requires_version_index;
    } requires[requires_count];

    u2 exports_count;
    {   u2 exports_index;
        u2 exports_flags;
        u2 exports_to_count;
        u2 exports_to_index[exports_to_count];
    } exports[exports_count];

    u2 opens_count;
    {   u2 opens_index;
        u2 opens_flags;
        u2 opens_to_count;
        u2 opens_to_index[opens_to_count];
    } opens[opens_count];

    u2 uses_count;
    u2 uses_index[uses_count];

    u2 provides_count;
    {   u2 provides_index;
        u2 provides_with_count;
        u2 provides_with_index[provides_with_count];
    } provides[provides_count];
}
 */
public final class ModuleAttribute extends AttributeInfo {
    public static ModuleAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new ModuleAttribute(attributeNameIndex, attributeLength);

        attribute.readCpIndex(reader, "module_name_index", ConstantModuleInfo.class);
        attribute.readAccessFlags(reader, "module_flags", AccessFlagType.AF_ALL);
        attribute.readCpIndex(reader, "module_version_index", ConstantUtf8Info.class);

        attribute.readU2TableLength(reader, "requires_count");
        attribute.readTable(reader, "requires", ModuleAttribute.RequiresInfo::readFrom);

        attribute.readU2TableLength(reader, "exports_count");
        attribute.readTable(reader, "exports", ModuleAttribute.ExportsInfo::readFrom);

        attribute.readU2TableLength(reader, "opens_count");
        attribute.readTable(reader, "opens", ModuleAttribute.OpensInfo::readFrom);

        attribute.readU2TableLength(reader, "uses_count");
        attribute.readTable(reader, "uses", it -> it.readCpIndexEager(ConstantClassInfo.class));

        attribute.readU2TableLength(reader, "provides_count");
        attribute.readTable(reader, "provides", ModuleAttribute.ProvidesInfo::readFrom, true);

        return attribute;
    }

    private ModuleAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class RequiresInfo extends ClassFileComponent {
        public static RequiresInfo readFrom(ClassFileReader reader) throws IOException {
            RequiresInfo requiresInfo = new RequiresInfo();
            var requiresIndex = requiresInfo.readCpIndex(reader, "requires_index", ConstantModuleInfo.class);
            var requiresFlags = requiresInfo.readAccessFlags(reader, "requires_flags", AccessFlagType.AF_MODULE_ATTR);
            requiresInfo.readCpIndex(reader, "requires_version_index", ConstantUtf8Info.class);

            requiresInfo.descProperty().bind(Val.combine(requiresIndex.constantInfoProperty(), requiresFlags.flagsProperty(), (info, flag) -> {
                if (info == null || info.getDescText() == null || flag == null) return null;

                StringBuilder builder = new StringBuilder();

                builder.append("requires ");

                if (flag.contains(AccessFlag.ACC_STATIC)) {
                    builder.append("static ");
                }
                if (flag.contains(AccessFlag.ACC_TRANSITIVE)) {
                    builder.append("transitive ");
                }

                builder.append(info.getDescText().replace('/', '.'));

                return new Label(builder.toString());
            }));

            return requiresInfo;
        }
    }

    public static final class ExportsInfo extends ClassFileComponent {
        public static ExportsInfo readFrom(ClassFileReader reader) throws IOException {
            ExportsInfo exportsInfo = new ExportsInfo();
            var exportsIndex = exportsInfo.readCpIndexEager(reader, "exports_index", ConstantPackageInfo.class);
            var exportsFlags = exportsInfo.readAccessFlags(reader, "exports_flags", AccessFlagType.AF_MODULE_ATTR);
            exportsInfo.readU2TableLength(reader, "exports_to_count");
            var exportsToIndex = exportsInfo.readTable(reader, "exports_to_index", it -> it.readCpIndexEager(ConstantModuleInfo.class), true);

            exportsInfo.descProperty().bind(
                    Val.map(
                            Val.flatMap(exportsIndex.constantInfoProperty(), it -> it == null ? null : it.descTextProperty()),
                            it -> it == null ? null : StringUtils.cutTextNode("exports " + it, Label::new))
            );

            return exportsInfo;
        }
    }

    public static final class OpensInfo extends ClassFileComponent {
        public static OpensInfo readFrom(ClassFileReader reader) throws IOException {
            OpensInfo opensInfo = new OpensInfo();
            var opensIndex = opensInfo.readCpIndex(reader, "opens_index", ConstantPackageInfo.class);
            opensInfo.readAccessFlags(reader, "opens_flags", AccessFlagType.AF_MODULE_ATTR);
            opensInfo.readU2TableLength(reader, "opens_to_count");
            opensInfo.readTable(reader, "opens_to_index", it -> it.readCpIndex(ConstantModuleInfo.class), true);

            opensInfo.descProperty().bind(
                    Val.map(
                            Val.flatMap(opensIndex.constantInfoProperty(), it -> it == null ? null : it.descTextProperty()),
                            it -> it == null ? null : StringUtils.cutTextNode("opens " + it, Label::new))
            );

            return opensInfo;
        }
    }

    public static final class ProvidesInfo extends ClassFileComponent {
        public static ProvidesInfo readFrom(ClassFileReader reader) throws IOException {
            ProvidesInfo providesInfo = new ProvidesInfo();
            var providesIndex = providesInfo.readCpIndex(reader, "provides_index", ConstantClassInfo.class);
            providesInfo.readU2TableLength(reader, "provides_with_count");
            providesInfo.readTable(reader, "provides_with_index", it -> it.readCpIndex(ConstantClassInfo.class), true);

            providesInfo.descProperty().bind(
                    Val.map(
                            Val.flatMap(providesIndex.constantInfoProperty(), it -> it == null ? null : it.descTextProperty()),
                            it -> it == null ? null : StringUtils.cutTextNode("provides " + it, Label::new))
            );

            return providesInfo;
        }
    }
}
