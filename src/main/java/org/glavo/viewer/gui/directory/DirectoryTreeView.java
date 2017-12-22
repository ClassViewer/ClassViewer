package org.glavo.viewer.gui.directory;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.MyTreeNode;
import org.glavo.viewer.gui.jar.JarTreeNode;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.function.Consumer;

public class DirectoryTreeView extends TreeView<MyTreeNode> {

    private final URL url;
    private Consumer<String> openClassHandler;

    public DirectoryTreeView(URL url, DirectoryTreeNode rootNode) {
        super(rootNode);
        this.url = url;
        rootNode.setExpanded(true);
        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedClass = getSelectedClass();
                if (selectedClass != null && openClassHandler != null) {
                    System.out.println(selectedClass);
                    openClassHandler.accept(selectedClass);
                }
            }
        });
        rootNode.setGraphic(new ImageView(FileType.FOLDER.icon));
    }

    public void setOpenClassHandler(Consumer<String> openClassHandler) {
        this.openClassHandler = openClassHandler;
    }


    private String getSelectedClass() {
        TreeItem<?> selected = getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.toString().endsWith(".class")) {

                String classUrl;
                if (selected instanceof DirectoryTreeNode) {
                    classUrl = "file:" + ((DirectoryTreeNode) selected).path;
                } else if (selected instanceof JarTreeNode) {
                    classUrl = "file:" + ((JarTreeNode) selected).path;
                } else {
                    return null;
                }
                classUrl = classUrl.replace('\\', '/');
                //System.out.println(classUrl);
                return classUrl;
            }
        }
        return null;
    }

}
