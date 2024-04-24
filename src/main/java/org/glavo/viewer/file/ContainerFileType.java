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

import javafx.scene.image.Image;

import java.io.IOException;
import java.util.Set;

public abstract non-sealed class ContainerFileType extends FileType {

    public ContainerFileType(String name, Set<String> extensions) {
        super(name, extensions);
    }

    protected ContainerFileType(String name, Image image, Set<String> extensions) {
        super(name, image, extensions);
    }

    public abstract Container openContainerImpl(FileHandle handle) throws IOException;
}
