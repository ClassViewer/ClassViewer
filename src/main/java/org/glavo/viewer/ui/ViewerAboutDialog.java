/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.glavo.viewer.Main;
import org.glavo.viewer.Metadata;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.util.logging.Log;

public final class ViewerAboutDialog extends BorderPane {
    public static final String HOME_URL = "https://github.com/ClassViewer/ClassViewer";

    private final Stage stage;

    public ViewerAboutDialog(Viewer viewer) {
        this.stage = new Stage();

        stage.setTitle(I18N.getString("about.title"));
        stage.getIcons().add(Images.loadImage("spy32"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        Scene scene = new Scene(this, 400, 200);
        Stylesheet.setStylesheet(scene);
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);

        ImageView image = new ImageView(Images.loadImage("spy128"));
        image.setOnMouseClicked(e -> {
            Log.info("Open Home Page");
            Main.getInstance().getHostServices().showDocument(HOME_URL);
            stage.close();
        });
        this.setCenter(image);

        var label = new VBox();
        {
            label.setAlignment(Pos.CENTER);
            label.setSpacing(8);
            BorderPane.setAlignment(label, Pos.CENTER);
            BorderPane.setMargin(label, new Insets(16));

            var version = new Text("ClassViewer " + Metadata.getAttribute("viewer.version", "SNAPSHOT"));
            version.getStyleClass().add("about-title");

            var copyright = new Text(I18N.getString("about.copyright"));
            copyright.setFill(Color.GRAY);

            label.getChildren().addAll(version, copyright);
        }

        this.setBottom(label);
    }

    public void show() {
        stage.show();
    }
}
