package org.glavo.viewer.gui.directory;

import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.jar.JarTreeLoader;
import org.glavo.viewer.gui.jar.JarTreeNode;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;

public class DirectoryTreeLoader {

    public static DirectoryTreeNode load(File jarFile) throws Exception {
        return path2node(jarFile.toPath());
    }

    private static DirectoryTreeNode path2node(Path path) throws IOException {
        DirectoryTreeNode node = new DirectoryTreeNode(path);

        Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path subPath, BasicFileAttributes attrs) throws IOException {
                if (Files.isDirectory(subPath)) {
                    DirectoryTreeNode subNode = path2node(subPath);
                    subNode.setGraphic(new ImageView(FileType.FOLDER.icon));
                    node.addSubNode(subNode);
                } else if (isClassFile(subPath)) {
                    DirectoryTreeNode n = new DirectoryTreeNode(subPath);
                    n.setGraphic(new ImageView(FileType.JAVA_CLASS.icon));
                    node.addSubNode(n);
                } else if (isJarFile(subPath)) {
                    try (FileSystem fs = FileSystems.newFileSystem(subPath, null)) {
                        JarTreeNode subNode = JarTreeLoader.path2node(fs.getPath("/"), subPath.getFileName().toString());
                        subNode.setJarPath(subPath.toString());
                        subNode.setGraphic(new ImageView(FileType.JAVA_JAR.icon));
                        node.addSubNode(subNode);
                    }
                }

                return FileVisitResult.CONTINUE;
            }

        });

        node.sortSubNodes();
        return node;
    }

    private static boolean isClassFile(Path p) {
        return p.toString().endsWith(".class");
    }

    private static boolean isJarFile(Path p) {
        return p.toString().endsWith(".jar");
    }

}
