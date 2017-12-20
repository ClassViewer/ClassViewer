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

    public static final Image copyImage = loadImage("/icons/copy.png");
    public static final Image openFileImage = loadImage("/icons/open.png");
    public static final Image packageImage = loadImage("/icons/package.png");
    public static final Image helpImage = loadImage("/icons/help.png");
    public static final Image methodImage = loadImage("/icons/method.png");
    public static final Image abstractMethodImage = loadImage("/icons/abstractMethod.png");
    public static final Image annotationImage = loadImage("/icons/annotationtype.png");
    public static final Image finalClassImage = loadImage("/icons/finalClass.png");
    public static final Image abstractClassImage = loadImage("/icons/abstractClass.png");
    public static final Image enumImage = loadImage("/icons/enum.png");
    public static final Image fieldImage = loadImage("/icons/field.png");
    public static final Image interfaceImage = loadImage("/icons/interface.png");
    public static final Image classImage = loadImage("/icons/class.png");
}
