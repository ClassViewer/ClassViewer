package org.glavo.viewer.gui.filetypes.classfile;

import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.glavo.viewer.classfile.ClassFile;
import org.glavo.viewer.classfile.ClassFileParser;
import org.glavo.viewer.gui.RecentFiles;
import org.glavo.viewer.gui.Viewer;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.util.FontUtils;
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
    public Tab open(Viewer viewer, URL url) throws Exception {
        Tab tab = new Tab(UrlUtils.getFileName(url));
        tab.setGraphic(new ImageView(icon));

        byte[] bytes = UrlUtils.readData(url);
        ClassFile classFile = new ClassFileParser().parse(bytes);
        ParsedViewerPane pane = new ParsedViewerPane(classFile, new HexText(bytes));

        RecentFiles.Instance.add(Instance, url);

        tab.setContent(pane);
        tab.setStyle(FontUtils.setUIFont(tab.getStyle()));
        tab.setUserData(url);
        return tab;
    }

    @Override
    public String toString() {
        return "JAVA_CLASS";
    }
}
