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
package org.glavo.viewer.file.containers.jimage;

import org.glavo.jimage.ImageReader;
import org.glavo.viewer.file.AbstractVirtualFile;
import org.glavo.viewer.file.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class JImageVirtualFile extends AbstractVirtualFile<JImageVirtualFile> {
    private static List<String> getName(JImageVirtualFile parent, ImageReader.Node node) {
        if (parent == null) {
            return List.of();
        }

        String fullName = node.getName();
        int idx = fullName.lastIndexOf('/');
        if (idx < 0 || idx == fullName.length() - 1) {
            throw new IllegalArgumentException("Path: " + fullName);
        }

        String simpleName = fullName.substring(idx + 1);

        String[] array = parent.name.toArray(new String[parent.name.size() + 1]);
        array[array.length - 1] = simpleName;
        return List.of(array);
    }

    private final ImageReader.Node node;

    JImageVirtualFile(Container container, JImageVirtualFile parent, ImageReader.Node node) {
        super(container, parent, getName(parent, node));
        this.node = node;
    }

    public ImageReader.Node getNode() {
        return node;
    }

    @Override
    public boolean isDirectory() {
        return node.resolveLink(true).isDirectory();
    }

    @Override
    public List<JImageVirtualFile> listFiles() throws IOException {
        ImageReader.Node node = this.node.resolveLink(true);

        if (!node.isDirectory()) {
            throw new IOException(this + " is not a directory");
        }

        if (!node.isCompleted()) {
            ((JImageContainer) container).getReader().findNode(node.getName());
        }

        List<JImageVirtualFile> list = new ArrayList<>();
        for (ImageReader.Node it : node.getChildren()) {
            JImageVirtualFile jImageVirtualFile = new JImageVirtualFile(container, this, it);
            list.add(jImageVirtualFile);
        }
        return list;
    }
}
