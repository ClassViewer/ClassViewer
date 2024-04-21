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
package org.glavo.viewer.file.types.java.classfile.jvm;

import org.jetbrains.annotations.Nullable;

// http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.5
//@formatter:off
public enum RefKind {

    REF_getField        (1),
    REF_getStatic       (2),
    REF_putField        (3),
    REF_putStatic       (4),
    REF_invokeVirtual   (5),
    REF_invokeStatic    (6),
    REF_invokeSpecial   (7),
    REF_newInvokeSpecial(8),
    REF_invokeInterface (9),
    ;

    private static final RefKind[] values = values();

    public final int kind;

    RefKind(int kind) {
        this.kind = kind;
    }

    public static @Nullable RefKind valueOf(int kind) {
        for (RefKind value : values) {
            if (value.kind == kind) {
                return value;
            }
        }

        return null;
    }

}
