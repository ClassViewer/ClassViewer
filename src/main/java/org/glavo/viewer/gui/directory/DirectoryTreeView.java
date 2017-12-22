package org.glavo.viewer.gui.directory;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.MyTreeNode;
import org.glavo.viewer.gui.jar.JarTreeNode;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

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
        String classUrl = null;
        if (selected instanceof DirectoryTreeNode) {
            if (selected.toString().endsWith(".class")) {
                classUrl = "file:" + ((DirectoryTreeNode) selected).path;
                classUrl = classUrl.replace('\\', '/');
            }
        } else {
            if (selected.toString().endsWith(".class")) {
                JarTreeNode node = (JarTreeNode) selected;
                classUrl = String.format("jar:file:%s!%s", node.jarPath, node.path).replace('\\', '/');
            }
        }
        return classUrl;
    }

}
