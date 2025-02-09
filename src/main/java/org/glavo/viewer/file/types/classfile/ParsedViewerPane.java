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
package org.glavo.viewer.file.types.classfile;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.glavo.viewer.FileComponent;
import org.glavo.viewer.ui.Viewer;
import org.glavo.viewer.file.types.binary.BytesBar;
import org.glavo.viewer.file.types.binary.HexPane;
import org.glavo.viewer.file.types.binary.HexText;
import org.glavo.viewer.util.logging.Log;

/**
 * Container of TreeView, HexPane, StatusBar and BytesBar.
 * <p>
 * |------------------------------|
 * | TreeView      |      HexPane |
 * |               |              |
 * |------------------------------|
 * | StatusLabel          BytesBar|
 * |------------------------------|
 */
public class ParsedViewerPane extends BorderPane {

    private Viewer viewer;

    private final TreeView<FileComponent> tree;
    private final SearchBar searchBar;
    private final HexPane hexPane;
    private final Label statusLabel;
    private final BytesBar bytesBar;
    private final Label rightLabel;

    public ParsedViewerPane(Viewer viewer, FileComponent file, HexText hex) {
        this.viewer = viewer;
        tree = buildClassTree(file);
        searchBar = new SearchBar(this);
        hexPane = new HexPane(hex);
        statusLabel = new Label(" ");
        rightLabel = new Label();
        rightLabel.setPrefWidth(10);

        bytesBar = new BytesBar(file.getLength());
        bytesBar.setMaxHeight(statusLabel.getMaxHeight());
        bytesBar.setPrefWidth(200);

        //this.setTop(searchBar);
        this.setCenter(buildSplitPane());
        this.setBottom(buildStatusBar());
        this.setRight(rightLabel);

        tree.getSelectionModel().getSelectedItems().addListener(this::selectItemAction);
    }

    private static TreeView<FileComponent> buildClassTree(FileComponent file) {
        file.setExpanded(true);

        TreeView<FileComponent> tree = new TreeView<>(file);
        tree.setMinWidth(200);
        return tree;
    }

    private SplitPane buildSplitPane() {
        SplitPane sp = new SplitPane();
        sp.getItems().add(tree);
        sp.getItems().add(hexPane);
        sp.setDividerPositions(0.36, 0.64);
        return sp;
    }

    private BorderPane buildStatusBar() {
        BorderPane statusBar = new BorderPane();
        statusBar.setLeft(statusLabel);
        statusBar.setRight(bytesBar);
        return statusBar;
    }

    private void selectItemAction(ListChangeListener.Change<? extends TreeItem<FileComponent>> c) {
        if (c.next() && c.wasAdded()) {
            TreeItem<FileComponent> node = c.getList().get(c.getFrom());
            if (node != null && node.getParent() != null) {
                FileComponent cc = node.getValue();
                Log.info("Select " + cc);
                statusLabel.setText(cc.toString());
                if (cc.getLength() > 0) {
                    hexPane.select(cc);
                    bytesBar.select(cc);
                }
            }
        }
    }

    public TreeView<FileComponent> getTree() {
        return tree;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public void showOrHideSearchBar() {
        if (this.getTop() == null) {
            this.setTop(searchBar);
        } else {
            this.setTop(null);
        }
    }

    public HexPane getHexPane() {
        return hexPane;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public BytesBar getBytesBar() {
        return bytesBar;
    }

    public Label getRightLabel() {
        return rightLabel;
    }

    public Viewer getViewer() {
        return viewer;
    }
}
