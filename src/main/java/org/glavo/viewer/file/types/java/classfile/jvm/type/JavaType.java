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

public abstract sealed class JavaType permits PrimitiveType, ArrayType, ClassType, MethodType {
    private final String qualified;
    private final String descriptor;

    JavaType(String qualified, String descriptor) {
        this.qualified = qualified;
        this.descriptor = descriptor;
    }

    public boolean isMethodType() {
        return this instanceof MethodType;
    }

    public String getQualified() {
        return qualified;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public int hashCode() {
        return descriptor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaType other && this.getDescriptor().equals(((JavaType) obj).getDescriptor());
    }

    @Override
    public String toString() {
        return getQualified();
    }
}
