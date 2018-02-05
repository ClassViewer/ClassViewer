package org.glavo.viewer.util;

import java.net.URL;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 *
 */
public class ImageUtils {
    public static ImageView createImageView(String imgName) {
        return new ImageView(loadImage(imgName));
    }

    public static Image loadImage(String imgName) {
        URL imgUrl = ImageUtils.class.getResource(imgName);
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

    //searchImage
    public static final Image searchImage = loadImage("/icons/search.png");
    public static final Image nextOccurenceImage = loadImage("/icons/nextOccurence.png");
    public static final Image previousOccurenceImage = loadImage("/icons/previousOccurence.png");

    public static final Image methodImage = loadImage("/icons/java/method.png");
    public static final Image abstractMethodImage = loadImage("/icons/java/abstractMethod.png");
    public static final Image annotationImage = loadImage("/icons/java/annotationtype.png");
    public static final Image finalClassImage = loadImage("/icons/java/finalClass.png");
    public static final Image abstractClassImage = loadImage("/icons/java/abstractClass.png");
    public static final Image enumImage = loadImage("/icons/java/enum.png");
    public static final Image fieldImage = loadImage("/icons/java/field.png");
    public static final Image interfaceImage = loadImage("/icons/interface.png");
    public static final Image classImage = loadImage("/icons/java/class.png");
    public static final Image attributeImage = loadImage("/icons/classfile/attribute.png");
    public static final Image exceptionImage = loadImage("/icons/java/exception.png");
    public static final Image javaSourceImage = loadImage("/icons/filetype/JavaSourceFile.png");
    public static final Image javaModuleImage = loadImage("/icons/javaModule.png");

    public static final Image privateImage = loadImage("/icons/classfile/c_private.png");
    public static final Image plocalImage = loadImage("/icons/classfile/c_plocal.png");
    public static final Image protectedImage = loadImage("/icons/classfile/c_protected.png");
    public static final Image publicImage = loadImage("/icons/classfile/c_public.png");

    public static final Image finalMarkImage = loadImage("/icons/finalMark.png");
    public static final Image errorMarkImage = loadImage("/icons/errorMark.png");
    public static final Image junitTestMarkImage = loadImage("/icons/junitTestMark.png");
    public static final Image runnableMarkImage = loadImage("/icons/runnableMark.png");
    public static final Image staticMarkImage = loadImage("/icons/staticMark.png");
}
