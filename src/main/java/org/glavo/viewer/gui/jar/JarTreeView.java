package org.glavo.viewer.gui.jar;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.glavo.viewer.util.Log;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Objects;
import java.util.function.Consumer;

public class JarTreeView extends TreeView<JarTreeNode> {

     final URL jarURL;
    private Consumer<String> openClassHandler;

    public JarTreeView(URL jarURL, JarTreeNode rootNode) {
        super(rootNode);
        this.jarURL = jarURL;
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

        this.setContextMenu(new JarTreeMenu(this));
    }

    public void setOpenClassHandler(Consumer<String> openClassHandler) {
        this.openClassHandler = openClassHandler;
    }


    // jar:file:/absolute/location/of/yourJar.jar!/path/to/ClassName.class
    String getSelectedClass() {
        TreeItem<JarTreeNode> selectedItem = getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            JarTreeNode selectedPath = selectedItem.getValue();
            if (selectedPath.toString().endsWith(".class")) {
                String classUrl = String.format("jar:%s!%s", jarURL, selectedPath.path);
                classUrl = classUrl.replace('\\', '/');
                //System.out.println(classUrl);
                return classUrl;
            }
        }
        return null;
    }

    public static boolean isOpen(File jarFile) throws Exception {
        URI jarUri = new URI("jar", jarFile.toPath().toUri().toString(), null);
        try {
            return FileSystems.getFileSystem(jarUri) != null;
        } catch (FileSystemNotFoundException e) {
            return false;
        }
    }

}
