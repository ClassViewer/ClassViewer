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
package org.glavo.viewer.util;

import sun.misc.Unsafe;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.ref.Cleaner;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public final class MemoryUtils {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Cleaner CLEANER = Cleaner.create(Thread.ofPlatform().name("Memory Cleaner", 0).factory());

    private static MethodHandle findHandle(String name, FunctionDescriptor descriptor, Linker.Option... options) {
        return Linker.nativeLinker().downcallHandle(
                Linker.nativeLinker().defaultLookup().find(name).orElseThrow(() -> new UnsatisfiedLinkError("Can't find symbol '" + name + "'")),
                descriptor, options
        );
    }

    private static int checkArraySize(long size) {
        if (size > Integer.MAX_VALUE || Integer.MAX_VALUE < 0) {
            throw new OutOfMemoryError("Invalid array size: " + size);
        }
        return (int) size;
    }

    public static MemorySegment allocate(long size) {
        int intSize = checkArraySize(size);
        return MemorySegment.ofArray(new byte[intSize]);
    }

    public static MemorySegment allocateDirect(long size) {
        long address = UNSAFE.allocateMemory(size);
        MemorySegment segment = MemorySegment.ofAddress(address);
        CLEANER.register(segment, () -> UNSAFE.freeMemory(address));
        return segment;
    }

    public static String newString(MemorySegment segment, Charset charset) {
        if (segment.heapBase().orElse(null) instanceof byte[] array) {
            return new String(array, (int) segment.address(), (int) segment.byteSize(), charset);
        } else {
            return new String(segment.toArray(ValueLayout.JAVA_BYTE), charset);
        }
    }
}
