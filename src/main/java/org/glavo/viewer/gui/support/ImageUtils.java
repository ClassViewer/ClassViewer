package org.glavo.viewer.gui.support;

import java.net.URL;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

    public static HBox images(Image... images) {
        HBox hbox = new HBox();
        for (Image image : images) {
            hbox.getChildren().add(new ImageView(image));
        }
        return hbox;
    }

    public static final Image copyImage = loadImage("/icons/copy.png");
    public static final Image openFileImage = loadImage("/icons/open.png");
    public static final Image openFolderImage = loadImage("/icons/openFolder.png");
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
    public static final Image javaSourceImage = loadImage("/icons/javaSource.png");

    public static final Image privateImage = loadImage("/icons/c_private.png");
    public static final Image plocalImage = loadImage("/icons/c_plocal.png");
    public static final Image protectedImage = loadImage("/icons/c_protected.png");
    public static final Image publicImage = loadImage("/icons/c_public.png");
}
