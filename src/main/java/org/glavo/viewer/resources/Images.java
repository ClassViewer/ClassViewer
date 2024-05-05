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
package org.glavo.viewer.resources;

import javafx.scene.image.Image;

public final class Images {
    public static Image loadImage(String imgName) {
        //noinspection ConstantConditions
        return new Image(Images.class.getResource("/org/glavo/viewer/resources/images/" + imgName).toExternalForm());
    }

    public static final Image logo16 = loadImage("viewer_16x16.png");
    public static final Image logo32 = loadImage("viewer_32x32.png");

    public static final Image file = loadImage("file.png");
    public static final Image folder = loadImage("folder.png");
    public static final Image archive = loadImage("archive.png");
    public static final Image fileStructure = loadImage("fileStructure.png");

    public static final Image failed = loadImage("failed.png");
    public static final Image unknown = loadImage("unknown.png");

    public static final Image menuOpen = loadImage("menu-open.png");

    private Images() {
    }
}
