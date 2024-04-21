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

import org.glavo.viewer.file.types.java.classfile.ClassFileComponent;
import org.glavo.viewer.file.types.java.classfile.ClassFileReader;
import org.glavo.viewer.file.types.java.classfile.constant.ConstantUtf8Info;
import org.glavo.viewer.file.types.java.classfile.datatype.CpIndex;
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
public final class RuntimeParameterAnnotationsAttribute extends AttributeInfo {
    public static RuntimeParameterAnnotationsAttribute readFrom(ClassFileReader reader, CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) throws IOException {
        var attribute = new RuntimeParameterAnnotationsAttribute(attributeNameIndex, attributeLength);
        attribute.readU1TableLength(reader, "num_parameters");
        attribute.readTable(reader, "parameter_annotations", RuntimeParameterAnnotationsAttribute.ParameterAnnotation::readFrom);
        return attribute;
    }

    private RuntimeParameterAnnotationsAttribute(CpIndex<ConstantUtf8Info> attributeNameIndex, U4 attributeLength) {
        super(attributeNameIndex, attributeLength);
    }

    public static final class ParameterAnnotation extends ClassFileComponent {
        public static ParameterAnnotation readFrom(ClassFileReader reader) throws IOException {
            ParameterAnnotation parameterAnnotationInfo = new ParameterAnnotation();
            parameterAnnotationInfo.readU2TableLength(reader, "num_annotations");
            parameterAnnotationInfo.readTable(reader, "annotations", RuntimeAnnotationsAttribute.Annotation::readFrom, true);
            return parameterAnnotationInfo;
        }
    }
}
