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
package org.glavo.viewer.file2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class PhysicalFile extends VirtualFile {
    private final Container container;
    private final Path path;

    public PhysicalFile(Container container, Path path) {
        this.container = container;
        this.path = path.toAbsolutePath().normalize();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public List<String> relativize(VirtualFile other) {
        if (other instanceof PhysicalFile file && this.getContainer().equals(file.getContainer())) {
            Path relativized = this.path.relativize(file.path);
            String[] paths = new String[relativized.getNameCount()];
            for (int i = 0; i < paths.length; i++) {
                paths[i] = relativized.getName(i).toString();
            }
            return List.of(paths);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getFileName() {
        return path.getFileName().toString();
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof PhysicalFile other
                && this.container.equals(other.container)
                && this.path.equals(other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(container, path);
    }

    @Override
    public String toString() {
        return "PhysicalFile[container=%s, path=%s]".formatted(container, path);
    }
}
