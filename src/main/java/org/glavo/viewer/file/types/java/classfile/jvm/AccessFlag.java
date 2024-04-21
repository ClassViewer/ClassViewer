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

import static org.glavo.viewer.file.types.java.classfile.jvm.AccessFlagType.*;

/**
 * Access and property flags of class, field, method and nested class.
 */
//@formatter:off
public enum AccessFlag {

    ACC_PUBLIC      (0x0001, AF_ALL                                ),
    ACC_PRIVATE     (0x0002, AF_FIELD | AF_METHOD | AF_NESTED_CLASS),
    ACC_PROTECTED   (0x0004, AF_FIELD | AF_METHOD | AF_NESTED_CLASS),
    ACC_STATIC      (0x0008, AF_FIELD | AF_METHOD | AF_NESTED_CLASS),
    ACC_FINAL       (0x0010, AF_ALL                                ),
    ACC_SUPER       (0x0020, AF_CLASS                              ),
    ACC_TRANSITIVE  (0x0020, AF_MODULE_ATTR                        ),
    ACC_SYNCHRONIZED(0x0020, AF_METHOD                             ),
    ACC_VOLATILE    (0x0040, AF_FIELD                              ),
    ACC_BRIDGE      (0x0040, AF_METHOD                             ),
    ACC_STATIC_PHASE(0x0040, AF_MODULE_ATTR                        ),
    ACC_TRANSIENT   (0x0080, AF_FIELD                              ),
    ACC_VARARGS     (0x0080, AF_METHOD                             ),
    ACC_NATIVE      (0x0100, AF_METHOD                             ),
    ACC_INTERFACE   (0x0200, AF_CLASS | AF_NESTED_CLASS            ),
    ACC_ABSTRACT    (0x0400, AF_CLASS | AF_METHOD | AF_NESTED_CLASS),
    ACC_STRICT      (0x0800, AF_METHOD                             ),
    ACC_SYNTHETIC   (0x1000, AF_ALL                                ),
    ACC_ANNOTATION  (0x2000, AF_CLASS | AF_NESTED_CLASS            ),
    ACC_ENUM        (0x4000, AF_CLASS | AF_FIELD | AF_NESTED_CLASS ),
    ACC_MODULE      (0x8000, AF_CLASS                              ),
    ACC_MANDATED    (0x8000, AF_MODULE_ATTR                        ),
    ;

    public final int flag;
    public final int type;

    AccessFlag(int flag, int type) {
        this.flag = flag;
        this.type = type;
    }

}
