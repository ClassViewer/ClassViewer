package org.glavo.viewer.gui.filetypes.jmod;

import javafx.stage.FileChooser;
import org.glavo.viewer.gui.filetypes.jar.JarFileType;
import org.glavo.viewer.util.ImageUtils;

import java.net.URL;

public final class JModFileType extends JarFileType {
    public static final JModFileType Instance = new JModFileType();

    private JModFileType() {
        super();
        this.icon = ImageUtils.loadImage("/icons/filetype/JModFile.png");
        this.filter = new FileChooser.ExtensionFilter("JMode File (*.jmod)", "*.jmod");
    }

    @Override
    public boolean accept(URL url) {
        String s = url.toString().toLowerCase();
        return s.endsWith(".jmod");
    }

    @Override
    public String toString() {
        return "JAVA_JMOD";
    }
}

