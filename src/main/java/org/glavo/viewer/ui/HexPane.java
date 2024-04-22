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

import javafx.scene.Node;
import kala.tuple.primitive.IntTuple2;
import org.glavo.viewer.annotation.FXThread;

import java.util.function.Consumer;

public interface HexPane {
    default Node getNode() {
        return (Node) this;
    }

    void select(int offset, int length);

    @FXThread
    default void setOnSelect(Consumer<IntTuple2> consumer) {
    }

    default Node getStatusBar() {
        return null;
    }

    default void setStatus(String status) {

    }
}
