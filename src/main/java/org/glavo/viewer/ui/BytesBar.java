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
package org.glavo.viewer.ui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.glavo.viewer.annotation.FXThread;

public final class BytesBar extends Pane {

    private final long byteCount;

    public BytesBar(long byteCount) {
        this.byteCount = byteCount;
    }

    @FXThread
    public void select(int offset, int length) {
        getChildren().clear();

        final double w = getWidth() - 4;
        final double h = getHeight();

        getChildren().setAll(new Line(0, h / 2, w, h / 2), new Rectangle(w * offset / byteCount, 4, w * length / byteCount, h - 8));
    }
}
