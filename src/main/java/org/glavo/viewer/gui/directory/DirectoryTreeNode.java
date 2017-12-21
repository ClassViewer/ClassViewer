package org.glavo.viewer.gui.directory;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.FileType;

import java.nio.file.Path;
import java.util.List;

public final class DirectoryTreeNode extends TreeItem<DirectoryTreeNode> {

    final String path;
    final String name;

    DirectoryTreeNode(Path path) {
        setValue(this);
        this.path = path.toString();
        if (path.getFileName() != null) {
            this.name = path.getFileName().toString();
        } else {
            this.name = path.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public List<DirectoryTreeNode> getSubNodes() {
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

    void addSubNode(DirectoryTreeNode node) {
        getSubNodes().add(node);
    }

    void sortSubNodes() {
        getSubNodes().sort(DirectoryTreeNode::comparePaths);
    }

    static int comparePaths(DirectoryTreeNode n1, DirectoryTreeNode n2) {
        if (n1.hasSubNodes() && !n2.hasSubNodes()) {
            return -1;
        } else if (!n1.hasSubNodes() && n2.hasSubNodes()) {
            return 1;
        } else {
            return n1.name.compareTo(n2.name);
        }
    }


}

