package org.glavo.viewer.gui.directory;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.function.Consumer;

public class DirectoryTreeView extends TreeView<DirectoryTreeNode> {

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
    }

    public void setOpenClassHandler(Consumer<String> openClassHandler) {
        this.openClassHandler = openClassHandler;
    }


    private String getSelectedClass() {
        TreeItem<DirectoryTreeNode> selectedItem = getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            DirectoryTreeNode selectedPath = selectedItem.getValue();
            if (selectedPath.toString().endsWith(".class")) {
                String classUrl = "file:" + selectedPath.path;
                classUrl = classUrl.replace('\\', '/');
                //System.out.println(classUrl);
                return classUrl;
            }
        }
        return null;
    }

}
