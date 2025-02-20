/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.glavo.viewer.file.types;

import javafx.scene.image.ImageView;
import org.glavo.viewer.resources.Images;
import org.glavo.viewer.ui.*;
import org.glavo.viewer.file.types.classfile.ClassFileType;
import org.glavo.viewer.file.types.jar.JarFileType;
import org.glavo.viewer.util.logging.Log;
import org.glavo.viewer.util.UrlUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public final class FolderType extends FileType {
    public static final FolderType Instance = new FolderType();

    public FileTreeNode load(URL url) throws URISyntaxException, IOException {
        Path path = Paths.get(url.toURI());

        FileTreeNode root = path2node(path);

        root.setUrl(url);
        root.setGraphic(new ImageView(Instance.icon));
        root.setDesc(url.toString());
        return root;
    }

    public FileTreeNode path2node(Path p) throws IOException {
        FileTreeNode node = new FileTreeNode();
        node.setUrl(UrlUtils.pathToUrl(p));
        node.setDesc(UrlUtils.getFileName(node.getUrl()));

        Files.walkFileTree(p, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path subPath, BasicFileAttributes attrs) throws IOException {
                URL subUrl = UrlUtils.pathToUrl(subPath);
                if (Files.isDirectory(subPath)) {
                    FileTreeNode subNode = path2node(subPath);
                    subNode.setGraphic(new ImageView(Instance.icon));
                    if (!subNode.getChildren().isEmpty()) {
                        node.getChildren().add(subNode);
                    }
                } else if (ClassFileType.Instance.accept(subUrl)) {
                    FileTreeNode subNode = new FileTreeNode();
                    subNode.setUrl(UrlUtils.pathToUrl(subPath));
                    subNode.setDesc(UrlUtils.getFileName(subNode.getUrl()));
                    subNode.setGraphic(new ImageView(ClassFileType.Instance.icon));
                    subNode.setUpdateMenu(subNode::setClassFileMenu);
                    node.getChildren().add(subNode);
                } else if (JarFileType.Instance.accept(subUrl)) {
                    try {
                        FileTreeNode subNode = JarFileType.Instance.load(subUrl);
                        subNode.setUrl(subUrl);
                        subNode.setGraphic(new ImageView(JarFileType.Instance.icon));
                        subNode.setDesc(UrlUtils.getFileName(subUrl));
                        node.getChildren().add(subNode);
                    } catch (URISyntaxException e) {
                        ViewerAlert.logAndShowExceptionAlert(e);
                    }

                }

                return FileVisitResult.CONTINUE;
            }
        });
        node.getChildren().sort((n1, n2) -> FileTreeNode.comparePaths((FileTreeNode) n1, (FileTreeNode) n2));
        return node;
    }

    public FolderType() {
        this.icon = Images.loadImage("folder");
    }

    @Override
    public boolean accept(URL url) {
        return url.toString().endsWith("/");
    }

    @Override
    public ViewerTab open(Viewer viewer, URL url) throws Exception {
        ViewerTab tab = ViewerTab.create(url);
        tab.setGraphic(new ImageView(icon));

        ViewerTask<FileTreeNode> task = new ViewerTask<>() {
            @Override
            protected FileTreeNode call() throws Exception {
                FileTreeNode root = load(url);
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
        return "FOLDER";
    }
}
