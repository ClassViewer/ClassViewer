package org.glavo.viewer.gui.filetypes.jar;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.glavo.viewer.gui.*;
import org.glavo.viewer.gui.filetypes.FileType;
import org.glavo.viewer.gui.filetypes.classfile.ClassFileType;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.Log;
import org.glavo.viewer.util.UrlUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class JarFileType extends FileType {
    public static final JarFileType Instance = new JarFileType();

    public FileTreeNode load(URL url) throws URISyntaxException, IOException {
        Path path = Paths.get(url.toURI());

        try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
            FileTreeNode root = path2node(fs.getPath("/"));

            root.setUrl(url);
            root.setGraphic(new ImageView(icon));
            root.setDesc(url.toString());
            return root;
        }
    }

    public FileTreeNode path2node(Path p) throws IOException {
        FileTreeNode node = new FileTreeNode();
        node.setUrl(UrlUtils.pathToUrl(p));
        node.setDesc(UrlUtils.getFileName(node.getUrl()));

        Files.walkFileTree(p, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path subPath, BasicFileAttributes attrs) throws IOException {
                if (Files.isDirectory(subPath)) {
                    FileTreeNode subNode = path2node(subPath);
                    subNode.setGraphic(new ImageView(ImageUtils.packageImage));
                    if (!subNode.getChildren().isEmpty()) {
                        node.getChildren().add(subNode);
                    }
                } else if (subPath.toString().toLowerCase().endsWith(".class")) {
                    FileTreeNode subNode = new FileTreeNode();
                    subNode.setUrl(UrlUtils.pathToUrl(subPath));
                    subNode.setDesc(UrlUtils.getFileName(subNode.getUrl()));
                    subNode.setGraphic(new ImageView(ClassFileType.Instance.icon));
                    subNode.setUpdateMenu(subNode::setClassFileMenu);
                    node.getChildren().add(subNode);
                }

                return FileVisitResult.CONTINUE;
            }
        });
        node.getChildren().sort((n1, n2) -> FileTreeNode.comparePaths((FileTreeNode) n1, (FileTreeNode) n2));
        return node;
    }

    protected JarFileType() {
        this.icon = ImageUtils.loadImage("/icons/filetype/JarFile.png");
        this.filter = new FileChooser.ExtensionFilter("Jar or Zip File (*.jar, *.zip)", "*.jar", "*.zip");
    }

    @Override
    public boolean accept(URL url) {
        String s = url.toString().toLowerCase();
        return s.endsWith(".jar") || s.endsWith(".zip");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) throws Exception {
        ViewerTab tab = ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<FileTreeNode> task = new ViewerTask<FileTreeNode>() {
            @Override
            protected FileTreeNode call() throws Exception {
                FileTreeNode root = JarFileType.this.load(url);
                root.setExpanded(true);
                return root;
            }
        };

        task.setOnSucceeded((FileTreeNode root) -> {
            FileTreeView view = new FileTreeView(viewer, root);
            view.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    FileTreeNode node = view.getSelected();
                    if (node != null && node.getUrl().toString().endsWith(".class")) {
                        Log.info("Open Class File: " + node.getUrl());
                        viewer.openFile(node.getUrl());
                    }
                }
            });
            tab.setContent(view);
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
        return "JAVA_JAR";
    }
}
