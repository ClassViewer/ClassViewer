package org.glavo.viewer.resources;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;

public class Images {
    public static Image loadImage(String imgName) {
        return new Image(Images.class.getResource("/org/glavo/viewer/resources/images/" + imgName).toExternalForm());
    }

    public static final Image logo16 = loadImage("viewer_16x16.png");
    public static final Image logo32 = loadImage("viewer_32x32.png");
}
