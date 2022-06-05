package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantModuleInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantPackageInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;
import org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType;

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
public class ModuleAttribute extends AttributeInfo {
    ModuleAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class RequiresInfo extends ClassFileComponent {
        public static RequiresInfo readFrom(ClassFileReader reader) throws IOException {
            RequiresInfo requiresInfo = new RequiresInfo();
            requiresInfo.readCpIndex(reader, "requires_index", ConstantModuleInfo.class);
            requiresInfo.readAccessFlags(reader, "requires_flags", AccessFlagType.AF_MODULE_ATTR);
            requiresInfo.readCpIndex(reader, "requires_version_index", ConstantUtf8Info.class);
            return requiresInfo;
        }
    }

    public static final class ExportsInfo extends ClassFileComponent {
        public static ExportsInfo readFrom(ClassFileReader reader) throws IOException {
            ExportsInfo exportsInfo = new ExportsInfo();
            exportsInfo.readCpIndex(reader, "exports_index", ConstantPackageInfo.class);
            exportsInfo.readAccessFlags(reader, "exports_flags", AccessFlagType.AF_MODULE_ATTR);
            exportsInfo.readU2TableLength(reader, "exports_to_count");
            exportsInfo.readTable(reader, "exports_to_index", it -> it.readCpIndex(ConstantModuleInfo.class), true);
            return exportsInfo;
        }
    }

    public static final class OpensInfo extends ClassFileComponent {
        public static OpensInfo readFrom(ClassFileReader reader) throws IOException {
            OpensInfo opensInfo = new OpensInfo();
            opensInfo.readCpIndex(reader, "opens_index", ConstantPackageInfo.class);
            opensInfo.readAccessFlags(reader, "opens_flags", AccessFlagType.AF_MODULE_ATTR);
            opensInfo.readU2TableLength(reader, "opens_to_count");
            opensInfo.readTable(reader, "opens_to_index", it -> it.readCpIndex(ConstantModuleInfo.class), true);
            return opensInfo;
        }
    }

    public static final class ProvidesInfo extends ClassFileComponent {
        public static ProvidesInfo readFrom(ClassFileReader reader) throws IOException {
            ProvidesInfo providesInfo = new ProvidesInfo();
            providesInfo.readCpIndex(reader, "provides_index", ConstantClassInfo.class);
            providesInfo.readU2TableLength(reader, "provides_with_count");
            providesInfo.readTable(reader, "provides_with_index", it -> it.readCpIndex(ConstantClassInfo.class), true);
            return providesInfo;
        }
    }
}
