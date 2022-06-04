package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.*;

import java.io.IOException;

/*
RuntimeVisibleTypeAnnotations_attribute {
    u2              attribute_name_index;
    u4              attribute_length;
    u2              num_annotations;
    type_annotation annotations[num_annotations];
}

RuntimeInvisibleTypeAnnotations_attribute {
    u2              attribute_name_index;
    u4              attribute_length;
    u2              num_annotations;
    type_annotation annotations[num_annotations];
}
 */
public class RuntimeTypeAnnotationsAttribute extends AttributeInfo {
    public RuntimeTypeAnnotationsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                                           U2 numAnnotations, Table<TypeAnnotation> annotations) {
        super(attributeNameIndex, attributeLength);
        numAnnotations.setName("num_annotations");
        annotations.setName("annotations");


        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, numAnnotations, annotations);
    }

    /*
    type_annotation {
        u1 target_type;
        union {
            type_parameter_target;
            supertype_target;
            type_parameter_bound_target;
            empty_target;
            formal_parameter_target;
            throws_target;
            localvar_target;
            catch_target;
            offset_target;
            type_argument_target;
        } target_info;
        type_path target_path;
        u2        type_index;
        u2        num_element_value_pairs;
        {   u2            element_name_index;
            element_value value;
        } element_value_pairs[num_element_value_pairs];
    }
     */
    public static final class TypeAnnotation extends ClassFileComponent {
        public static TypeAnnotation readFrom(ClassFileReader reader) throws IOException {
            TypeAnnotation typeAnnotation = new TypeAnnotation();
            U1Hex targetType = typeAnnotation.readU1Hex(reader, "target_type");
            typeAnnotation.read(reader, "target_info", r -> TargetInfo.readFrom(r, targetType));
            typeAnnotation.read(reader, "target_path", TypePath::readFrom);
            typeAnnotation.read(reader, "annotation", RuntimeAnnotationsAttribute.Annotation::readFrom);
            return typeAnnotation;
        }
    }

    public static final class TargetInfo extends ClassFileComponent {
        public static TargetInfo readFrom(ClassFileReader reader, UInt targetType) throws IOException {
            TargetInfo targetInfo = new TargetInfo();
            switch (targetType.getIntValue()) {
                /*
                type_parameter_target {
                    u1 type_parameter_index;
                }
                 */
                case 0x00, 0x01 -> targetInfo.readU1(reader, "type_parameter_index");

                /*
                supertype_target {
                    u2 supertype_index;
                }
                 */
                case 0x10 -> targetInfo.readU2(reader, "supertype_index");

                /*
                type_parameter_bound_target {
                    u1 type_parameter_index;
                    u1 bound_index;
                }
                 */
                case 0x11, 0x12 -> {
                    targetInfo.readU1(reader, "type_parameter_index");
                    targetInfo.readU1(reader, "bound_index");
                }

                /*
                empty_target {
                }
                 */
                case 0x13, 0x14, 0x15 -> {
                }

                /*
                formal_parameter_target {
                    u1 formal_parameter_index;
                }
                 */
                case 0x16 -> targetInfo.readU1(reader, "formal_parameter_index");

                /*
                throws_target {
                    u2 throws_type_index;
                }
                 */
                case 0x17 -> targetInfo.readU2(reader, "throwsTypeIndex");

                /*
                localvar_target {
                    u2 table_length;
                    {   u2 start_pc;
                        u2 length;
                        u2 index;
                    } table[table_length];
                }
                 */
                case 0x40, 0x41 -> {
                    targetInfo.readU2TableLength(reader, "table_length");
                    targetInfo.readTable(reader, "table", LocalVarInfo::readFrom, true);
                }

                /*
                catch_target {
                    u2 exception_table_index;
                }
                 */
                case 0x42 -> targetInfo.readU2(reader, "exception_table_index");

                /*
                offset_target {
                    u2 offset;
                }
                 */
                case 0x43, 0x44, 0x45, 0x46 -> targetInfo.readU2(reader, "offset");

                /*
                type_argument_target {
                    u2 offset;
                    u1 type_argument_index;
                }
                 */
                case 0x47, 0x48, 0x49, 0x4A, 0x4B -> {
                    targetInfo.readU2(reader, "offset");
                    targetInfo.readU1(reader, "type_argument_index");
                }
            }
            return targetInfo;
        }
    }

    public static final class LocalVarInfo extends ClassFileComponent {
        public static LocalVarInfo readFrom(ClassFileReader reader) throws IOException {
            LocalVarInfo localVarInfo = new LocalVarInfo();
            localVarInfo.readU2(reader, "start_pc");
            localVarInfo.readU2(reader, "length");
            localVarInfo.readU2(reader, "index");
            return localVarInfo;
        }
    }

    /*
    type_path {
        u1 path_length;
        {   u1 type_path_kind;
            u1 type_argument_index;
        } path[path_length];
    }
     */
    public static final class TypePath extends ClassFileComponent {
        public static TypePath readFrom(ClassFileReader reader) throws IOException {
            TypePath typePath = new TypePath();
            typePath.readU1TableLength(reader, "path_length");
            typePath.readTable(reader, "path", PathInfo::readFrom, true);
            return typePath;
        }
    }

    public static final class PathInfo extends ClassFileComponent {
        public static PathInfo readFrom(ClassFileReader reader) throws IOException {
            PathInfo pathInfo = new PathInfo();
            pathInfo.readU1(reader, "type_path_kind");
            pathInfo.readU1(reader, "type_argument_index");
            return pathInfo;
        }
    }
}
