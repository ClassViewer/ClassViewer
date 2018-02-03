package org.glavo.viewer.gui.filetypes.binary;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.glavo.viewer.gui.*;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;

public class BinaryFileType extends FileType {
    public static final BinaryFileType Instance = new BinaryFileType();

    private BinaryFileType() {
        this.filter = null;
        this.icon = ImageUtils.loadImage("/icons/filetype/UnknownFile.png");
    }

    @Override
    public boolean accept(URL url) {
        return url != null && !url.toString().endsWith("/");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) {
        ViewerTab tab =  ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<HexText> task = new ViewerTask<HexText>() {
            @Override
            protected HexText call() throws Exception {
                byte[] bytes = UrlUtils.readData(url);
                return new HexText(bytes);
            }
        };
        task.setOnSucceeded((HexText text) -> {
            tab.setContent(new HexPane(text));
            RecentFiles.Instance.add(this, url);
        });

        task.setOnFailed((Throwable ex) -> {
            viewer.getTabPane().getTabs().remove(tab);
            ViewerAlert.logAndShowExceptionAlert(ex);
        });

        task.startInNewThread();
        return tab;
    }

    @Override
    public String toString() {
        return "BINARY_FILE";
    }
}
