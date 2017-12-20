package org.glavo.viewer.gui.jar;

import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;

public class JarTreeLoader {

    public static JarTreeNode load(File jarFile) throws Exception {
        URI jarUri = new URI("jar", jarFile.toPath().toUri().toString(), null);
        try (FileSystem zipFs = FileSystems.newFileSystem(jarUri, new HashMap<>())) {
            return path2node(zipFs.getPath("/"));
        }
    }

    private static JarTreeNode path2node(Path path) throws IOException {
        JarTreeNode node = new JarTreeNode(path);

        Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path subPath, BasicFileAttributes attrs) throws IOException {
                if (Files.isDirectory(subPath)) {
                    JarTreeNode subNode = path2node(subPath);
                    subNode.setGraphic(new ImageView(ImageUtils.packageImage));
                    if (subNode.hasSubNodes()) {
                        node.addSubNode(subNode);
                    }
                } else if (isClassFile(subPath)) {
                    JarTreeNode n = new JarTreeNode(subPath);
                    n.setGraphic(new ImageView(FileType.JAVA_CLASS.icon));
                    node.addSubNode(n);
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

}
