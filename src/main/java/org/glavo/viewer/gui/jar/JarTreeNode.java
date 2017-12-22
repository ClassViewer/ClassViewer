package org.glavo.viewer.gui.jar;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.MyTreeNode;
import org.glavo.viewer.gui.support.FileType;

import java.nio.file.Path;
import java.util.List;

public final class JarTreeNode extends TreeItem<JarTreeNode> implements MyTreeNode {
    public String jarPath = null;
    public final String path;
    public final String name;

    public JarTreeNode(Path path) {
        setValue(this);
        this.path = path.toString();
        if (path.getFileName() != null) {
            String s = path.getFileName().toString();
            if (s.endsWith("/")) {
                s = s.substring(0, s.length() - 1);
            }
            this.name = s;
        } else {
            String s = path.toString();
            if (s.equals("/")) {
                this.name = "";
                this.setGraphic(new ImageView(FileType.JAVA_JAR.icon));
            } else {

                this.name = s;
            }
        }
    }

    public JarTreeNode(Path path, String name) {
        setValue(this);
        this.path = path.toString();
        this.name = name;
        if (path.getFileName() == null) {
            String s = path.toString();
            if (s.equals("/")) {
                this.setGraphic(new ImageView(FileType.JAVA_JAR.icon));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<JarTreeNode> getSubNodes() {
        return (List) super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        return getSubNodes().isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasSubNodes() {
        return !getSubNodes().isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
        getSubNodes().forEach(it -> it.setJarPath(jarPath));
    }

    void addSubNode(JarTreeNode node) {
        getSubNodes().add(node);
    }

    void sortSubNodes() {
        getSubNodes().sort(JarTreeNode::comparePaths);
    }

    public static int comparePaths(JarTreeNode n1, JarTreeNode n2) {
        if (n1.hasSubNodes() && !n2.hasSubNodes()) {
            return -1;
        } else if (!n1.hasSubNodes() && n2.hasSubNodes()) {
            return 1;
        } else {
            return n1.name.compareTo(n2.name);
        }
    }


}

