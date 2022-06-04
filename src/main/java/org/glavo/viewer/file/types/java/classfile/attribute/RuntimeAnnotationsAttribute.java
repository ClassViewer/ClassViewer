package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileParseException;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantClassInfo;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantValueInfo;
import org.glavo.viewer.file.types.java.classfile.datatype.*;

import java.io.IOException;

/*
RuntimeVisibleAnnotations_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 num_annotations;
    annotation annotations[num_annotations];
}

RuntimeInvisibleAnnotations_attribute {
    u2         attribute_name_index;
    u4         attribute_length;
    u2         num_annotations;
    annotation annotations[num_annotations];
}
 */
public class RuntimeAnnotationsAttribute extends AttributeInfo {

    public RuntimeAnnotationsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                                       U2 numAnnotations, Table<Annotation> annotations) {
        super(attributeNameIndex, attributeLength);

        numAnnotations.setName("num_annotations");
        annotations.setName("annotations");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, numAnnotations, annotations);
    }

    /*
    annotation {
        u2 type_index;
        u2 num_element_value_pairs;
        {   u2            element_name_index;
            element_value value;
        } element_value_pairs[num_element_value_pairs];
    }
     */
    public static final class Annotation extends ClassFileComponent {
        public static Annotation readFrom(ClassFileReader reader) throws IOException {
            Annotation annotation = new Annotation();
            annotation.readCpIndex(reader, "type_index", ConstantUtf8Info.class);
            annotation.readTableLength(reader, "num_element_value_pairs");
            annotation.readTable(reader, "element_value_pairs", ElementValuePair::readFrom, true);
            return annotation;
        }

    }

    public static final class ElementValuePair extends ClassFileComponent {
        public static ElementValuePair readFrom(ClassFileReader reader) throws IOException {
            ElementValuePair elementValuePair = new ElementValuePair();
            elementValuePair.readU2(reader, "element_name_index");
            elementValuePair.read(reader, "value", ElementValue::readFrom);
            return elementValuePair;
        }
    }

    /*
    element_value {
        u1 tag;
        union {
            u2 const_value_index;

            {   u2 type_name_index;
                u2 const_name_index;
            } enum_const_value;

             u2 class_info_index;

            annotation annotation_value;

            {   u2            num_values;
                element_value values[num_values];
            } array_value;
        } value;
    }
      */
    public static final class ElementValue extends ClassFileComponent {
        public static ElementValue readFrom(ClassFileReader reader) throws IOException {
            ElementValue elementValue = new ElementValue();
            U1 tag = elementValue.readU1(reader, "tag");

            switch (tag.getIntValue()) {
                case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' ->
                        elementValue.readCpIndex(reader, "const_value_index", ConstantValueInfo.class);
                case 's' -> elementValue.readCpIndex(reader, "const_value_index", ConstantUtf8Info.class);
                case 'e' -> elementValue.read(reader, "enum_const_value", EnumConstValue::readFrom);
                case 'c' -> elementValue.readCpIndex(reader, "class_info_index", ConstantClassInfo.class);
                case '@' -> elementValue.read(reader, "annotation_value", Annotation::readFrom);
                case '[' -> elementValue.read(reader, "array_value", ArrayValue::readFrom);
                default -> throw new ClassFileParseException("Unknown tag: " + tag.getIntValue());
            }

            return elementValue;
        }
    }

    public static final class EnumConstValue extends ClassFileComponent {
        public static EnumConstValue readFrom(ClassFileReader reader) throws IOException {
            EnumConstValue enumConstValue = new EnumConstValue();
            enumConstValue.readU2(reader, "type_name_index");
            enumConstValue.readU2(reader, "const_name_index");
            return enumConstValue;
        }
    }

    public static final class ArrayValue extends ClassFileComponent {
        public static ArrayValue readFrom(ClassFileReader reader) throws IOException {
            ArrayValue arrayValue = new ArrayValue();
            arrayValue.readTableLength(reader, "num_values");
            arrayValue.readTable(reader, "values", ElementValue::readFrom, true);
            return arrayValue;
        }
    }
}
