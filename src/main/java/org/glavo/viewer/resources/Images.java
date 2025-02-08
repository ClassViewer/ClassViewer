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
import javafx.scene.image.ImageView;

public final class Images {

    public static final Image ICON_PACKAGE = loadImage("package");
    public static final Image ICON_ARCHIVE_FILE = loadImage("fileTypes/archive");
    public static final Image ICON_UNKNOWN_FILE = loadImage("fileTypes/unknown");

    public static Image loadImage(String imgName) {
        return new Image(Resources.getResource("/org/glavo/viewer/resources/images/" + imgName + ".png").toExternalForm());
    }

    public static ImageView createImageView(String imgName) {
        return new ImageView(loadImage(imgName));
    }

    private Images() {
    }
}
