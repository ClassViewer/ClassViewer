package org.glavo.viewer.gui.filetypes.classfile;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.glavo.viewer.classfile.ClassFile;
import org.glavo.viewer.classfile.ClassFileParser;
import org.glavo.viewer.gui.RecentFiles;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.ViewerAlert;
import org.glavo.viewer.gui.ViewerTask;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;
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

    @SuppressWarnings("unchecked")
    @Override
    public Tab open(Viewer viewer, URL url) throws Exception {
        Tab tab = new Tab(UrlUtils.getFileName(url));
        tab.setGraphic(new ImageView(icon));
        tab.setContent(new BorderPane(new ProgressBar()));

        ViewerTask<Pair<ClassFile, byte[]>> task = new ViewerTask<Pair<ClassFile, byte[]>>() {
            @Override
            protected Pair<ClassFile, byte[]> call() throws Exception {
                byte[] bytes = UrlUtils.readData(url);
                ClassFile classFile = new ClassFileParser().parse(bytes);
                RecentFiles.Instance.add(Instance, url);
                return new Pair<>(classFile, bytes);
            }
        };
        task.setOnSucceeded((Pair<ClassFile, byte[]> value) -> {
            tab.setContent(new ParsedViewerPane(value.getKey(), new HexText(value.getValue())));
            tab.setStyle(FontUtils.setUIFont(tab.getStyle()));
            tab.setUserData(url);
            RecentFiles.Instance.add(Instance, url);
        });
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
