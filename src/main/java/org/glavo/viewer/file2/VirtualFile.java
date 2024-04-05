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

import java.util.List;

public abstract non-sealed class VirtualFile extends VirtualPath {

    public abstract Container getContainer();

    /**
     * @throws IllegalArgumentException if other is not a Path that can be relativized against this path
     */
    public abstract List<String> relativize(VirtualFile other);

    public abstract String getFileName();

    private String extension;

    public String getFileNameExtension() {
        if (extension == null) {
            String fn = getFileName();
            int idx = fn.lastIndexOf('.');
            extension = idx <= 0 ? "" : fn.substring(idx + 1);
        }

        return extension;
    }

    // ---

    public abstract boolean isDirectory();

}
