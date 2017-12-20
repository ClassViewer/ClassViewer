package org.glavo.viewer.gui.jar;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.FileType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JarTreeNode extends TreeItem<JarTreeNode> {

    final String path;
    final String name;

    JarTreeNode(Path path) {
        setValue(this);
        this.path = path.toString();
        if (path.getFileName() != null) {

            this.name = path.getFileName().toString();

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

    boolean hasSubNodes() {
        return !getSubNodes().isEmpty();
    }

    void addSubNode(JarTreeNode node) {
        getSubNodes().add(node);
    }

    void sortSubNodes() {
        getSubNodes().sort(JarTreeNode::comparePaths);
    }

    static int comparePaths(JarTreeNode n1, JarTreeNode n2) {
        if (n1.hasSubNodes() && !n2.hasSubNodes()) {
            return -1;
        } else if (!n1.hasSubNodes() && n2.hasSubNodes()) {
            return 1;
        } else {
            return n1.name.compareTo(n2.name);
        }
    }

}
