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

import java.lang.ref.WeakReference;
import java.util.HashMap;

public final class Images {

    private static final HashMap<String, WeakReference<Image>> imageCache = new HashMap<>();

    private static Image loadImageImpl(String imgName) {
        return new Image(Resources.getResource("/org/glavo/viewer/resources/images/" + imgName + ".png").toExternalForm());
    }

    public static Image loadImage(String imgName) {
        WeakReference<Image> reference = imageCache.get(imgName);
        Image image;
        if (reference != null && (image = reference.get()) != null) {
            return image;
        }

        synchronized (imageCache) {
            reference = imageCache.get(imgName);
            if (reference != null && (image = reference.get()) != null) {
                return image;
            }

            image = loadImageImpl(imgName);
            imageCache.put(imgName, new WeakReference<>(image));
            return image;
        }
    }

    private Images() {
    }
}
