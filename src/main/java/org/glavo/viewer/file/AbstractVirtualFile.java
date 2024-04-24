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
package org.glavo.viewer.file;

import java.util.List;

public abstract class AbstractVirtualFile<T extends AbstractVirtualFile<T>> extends VirtualFile {
    protected final List<String> name;
    protected final T parent;

    protected AbstractVirtualFile(Container container, T parent, List<String> name) {
        super(container);
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getFileName() {
        return name.isEmpty() ? "" : name.getLast();
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        if (!(other instanceof AbstractVirtualFile<?> otherZipVirtualFile) || this.container != otherZipVirtualFile.container) {
            throw new IllegalArgumentException("File: " + other);
        }

        return relativize(this.name, otherZipVirtualFile.name);
    }

    @Override
    public T getParent() {
        return parent;
    }
}
