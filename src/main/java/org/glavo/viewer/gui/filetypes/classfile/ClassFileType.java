package org.glavo.viewer.gui.filetypes.classfile;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.glavo.viewer.classfile.ClassFile;
import org.glavo.viewer.classfile.ClassFileComponent;
import org.glavo.viewer.classfile.ClassFileParser;
import org.glavo.viewer.gui.*;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.gui.filetypes.binary.HexText;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.UrlUtils;

import java.net.URL;
import java.util.Objects;

public final class ClassFileType extends FileType {
    public static final ClassFileType Instance = new ClassFileType();

    private ClassFileType() {
        this.filter = new FileChooser.ExtensionFilter("Java Class File (*.class)", "*.class");
        this.icon = ImageUtils.loadImage("/icons/filetype/ClassFile.png");
    }

    @Override
    public boolean accept(URL url) {
        Objects.requireNonNull(url);
        return url.toString().toLowerCase().endsWith(".class");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) {
        ViewerTab tab = ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<Void> task = new ViewerTask<Void>() {
            @Override
            protected Void call() throws Exception {
                byte[] bytes = UrlUtils.readData(url);
                ClassFile classFile = new ClassFileParser().parse(bytes);
                RecentFiles.Instance.add(Instance, url);
                HexText text = new HexText(bytes);
                Platform.runLater(() -> {
                    ParsedViewerPane pane = new ParsedViewerPane(viewer, classFile, text);
                    ((ClassFileComponent) pane.getTree().getRoot()).setName(UrlUtils.getClassName(url));
                    tab.setContent(pane);
                    tab.getUserData().showOrHideSearchBar = pane::showOrHideSearchBar;
                    RecentFiles.Instance.add(Instance, url);
                });
                return null;
            }
        };
        task.setOnFailed((Throwable e) -> {
            viewer.getTabPane().getTabs().remove(tab);
            ViewerAlert.logAndShowExceptionAlert(e);
        });

        task.startInNewThread();
        return tab;
    }

    @Override
    public String toString() {
        return "JAVA_CLASS";
    }
}
