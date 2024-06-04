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
package org.glavo.viewer.file.types.java.classfile2.base;

import org.glavo.viewer.file.types.java.classfile2.ClassFileComponent;

public final class IntComponent extends ClassFileComponent {
    private final boolean signed;
    private final boolean hex;

    private long longValue;

    public IntComponent(boolean signed, boolean hex, int length) {
        assert length == 1 || length == 2 || length == 4;

        this.signed = signed;
        this.hex = hex;
        this.length = length;
    }

    public boolean isSigned() {
        return signed;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

}
