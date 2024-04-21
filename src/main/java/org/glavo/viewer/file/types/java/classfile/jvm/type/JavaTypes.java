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
package org.glavo.viewer.file.types.java.classfile.jvm.type;

import kala.value.primitive.IntVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class JavaTypes {
    private JavaTypes() {
    }

    public static final PrimitiveType VOID = new PrimitiveType("void", "V");

    public static final PrimitiveType BYTE = new PrimitiveType("byte", "B");
    public static final PrimitiveType CHAR = new PrimitiveType("char", "C");
    public static final PrimitiveType DOUBLE = new PrimitiveType("double", "D");
    public static final PrimitiveType FLOAT = new PrimitiveType("float", "F");
    public static final PrimitiveType INT = new PrimitiveType("int", "I");
    public static final PrimitiveType LONG = new PrimitiveType("long", "J");
    public static final PrimitiveType SHORT = new PrimitiveType("short", "S");
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", "Z");

    public static ArrayType newArrayType(JavaType baseType, int level) {
        assert level >= 1;

        String qualified = baseType.getQualified() + "[]".repeat(level);
        String descriptor = "[".repeat(level) + baseType.getDescriptor();

        return new ArrayType(baseType, level, qualified, descriptor);
    }

    public static MethodType newMethodType(JavaType returnType, List<JavaType> parameterTypes) {
        String qualified = parameterTypes.stream().map(JavaType::getQualified).collect(Collectors.joining(
                ", ", "(", "): " + returnType.getQualified()
        ));

        String descriptor = parameterTypes.stream().map(JavaType::getDescriptor).collect(Collectors.joining(
                "", "(", ")" + returnType.getDescriptor()
        ));

        return new MethodType(returnType, parameterTypes, qualified, descriptor);
    }

    public static ClassType classTypeOfDescriptor(String descriptor) {
        assert descriptor.length() > 2;
        assert descriptor.charAt(0) == 'L';
        assert descriptor.charAt(descriptor.length() - 1) == ';';

        String qualified = descriptor.substring(1, descriptor.length() - 1).replace('/', '.');

        return new ClassType(qualified, descriptor);
    }

    public static JavaType parseDescriptor(String descriptor) {
        if (descriptor == null || descriptor.isEmpty()) throw new IllegalArgumentException("descriptor is empty");

        IntVar endIdx = new IntVar();

        if (descriptor.charAt(0) == '(') {
            endIdx.value = 1;

            List<JavaType> parTypes;
            if (descriptor.length() >= 3 && descriptor.charAt(1) == ')') {
                parTypes = List.of();
            } else {
                parTypes = new ArrayList<>();
                do {
                    parTypes.add(scanDescriptor(descriptor, endIdx.value, endIdx));
                } while (endIdx.value < descriptor.length() && descriptor.charAt(endIdx.value) != ')');
            }
            if (endIdx.value < descriptor.length() && descriptor.charAt(endIdx.value) == ')') {
                JavaType retType = scanDescriptor(descriptor, endIdx.value + 1, endIdx);
                if (endIdx.value == descriptor.length())
                    return newMethodType(retType, Collections.unmodifiableList(parTypes));
            }

        } else {
            JavaType type = scanDescriptor(descriptor, 0, endIdx);
            if (endIdx.value == descriptor.length()) return type;
        }

        throw new IllegalArgumentException("malformed descriptor '" + descriptor + "'");
    }

    public static JavaType scanDescriptor(String descriptor, int begin, IntVar endIdx) {
        int len = descriptor.length() - begin;
        if (len == 0) throw new IllegalArgumentException("descriptor is empty");

        switch (descriptor.charAt(begin)) {
            case 'B' -> {
                endIdx.value = begin + 1;
                return BYTE;
            }
            case 'C' -> {
                endIdx.value = begin + 1;
                return CHAR;
            }
            case 'D' -> {
                endIdx.value = begin + 1;
                return DOUBLE;
            }
            case 'F' -> {
                endIdx.value = begin + 1;
                return FLOAT;
            }
            case 'I' -> {
                endIdx.value = begin + 1;
                return INT;
            }
            case 'J' -> {
                endIdx.value = begin + 1;
                return LONG;
            }
            case 'S' -> {
                endIdx.value = begin + 1;
                return SHORT;
            }
            case 'Z' -> {
                endIdx.value = begin + 1;
                return BOOLEAN;
            }
            case 'V' -> {
                endIdx.value = begin + 1;
                return VOID;
            }
            case 'L' -> {
                int idx = descriptor.indexOf(';', begin + 1);
                if (idx > 0) {
                    endIdx.value = idx + 1;
                    return classTypeOfDescriptor(descriptor.substring(begin, idx + 1));
                }
            }
            case '[' -> {
                int level = 0;
                int idx = begin;
                while (idx < descriptor.length()) {
                    if (descriptor.charAt(idx) != '[') break;
                    level++;
                    idx++;
                }
                if (idx != descriptor.length()) {
                    JavaType tpe = scanDescriptor(descriptor, idx, endIdx);
                    return newArrayType(tpe, level);
                }
            }
        }

        throw new IllegalArgumentException("malformed descriptor '" + descriptor.substring(begin) + "'");
    }
}
