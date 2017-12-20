package org.glavo.viewer.gui.support;

import java.net.URL;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.AboutDialog;

/**
 *
 */
public class ImageUtils {

    public static ImageView createImageView(String imgName) {
        return new ImageView(loadImage(imgName));
    }

    public static Image loadImage(String imgName) {
        URL imgUrl = AboutDialog.class.getResource(imgName);
        return new Image(imgUrl.toString());
    }

    public static final Image copyImage = loadImage("/copy.png");
    public static final Image openFileImage = loadImage("/open.png");
    public static final Image openFileImage2 = loadImage("/open2.png");
    public static final Image helpImage = loadImage("/help.png");
}
