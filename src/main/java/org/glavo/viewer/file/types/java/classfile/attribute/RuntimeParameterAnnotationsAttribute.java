package org.glavo.viewer.file.types.java.classfile.attribute;

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
import org.glavo.viewer.file.types.java.classfile.datatype.Table;
import org.glavo.viewer.file.types.java.classfile.datatype.U1;
import org.glavo.viewer.file.types.java.classfile.datatype.U4;

import java.io.IOException;

/*
RuntimeVisibleParameterAnnotations_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 num_parameters;
    {   u2         num_annotations;
        annotation annotations[num_annotations];
    } parameter_annotations[num_parameters];
}

RuntimeInvisibleParameterAnnotations_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 num_parameters;
    {   u2         num_annotations;
        annotation annotations[num_annotations];
    } parameter_annotations[num_parameters];
}
 */
public class RuntimeParameterAnnotationsAttribute extends AttributeInfo {

    public RuntimeParameterAnnotationsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength,
                                                U1 numParameters, Table<ParameterAnnotation> parameterAnnotations) {
        super(attributeNameIndex, attributeLength);

        numParameters.setName("num_parameters");
        parameterAnnotations.setName("parameter_annotations");

        //noinspection unchecked
        this.getChildren().setAll(attributeNameIndex, attributeLength, numParameters, parameterAnnotations);

    }

    public static final class ParameterAnnotation extends ClassFileComponent {
        public static ParameterAnnotation readFrom(ClassFileReader reader) throws IOException {
            ParameterAnnotation parameterAnnotationInfo = new ParameterAnnotation();
            U1 numAnnotations = parameterAnnotationInfo.readU1(reader, "num_annotations");
            parameterAnnotationInfo.readTable(reader, "annotations", numAnnotations, RuntimeAnnotationsAttribute.Annotation::readFrom, true);
            return parameterAnnotationInfo;
        }
    }
}
