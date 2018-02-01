package org.glavo.viewer.gui.filetypes.classfile;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.glavo.viewer.FileComponent;
import org.glavo.viewer.util.FontUtils;
import org.glavo.viewer.util.Log;

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

    private final TreeView<FileComponent> tree;
    //private final SearchBar searchBar;
    private final HexPane hexPane;
    private final Label statusLabel;
    private final BytesBar bytesBar;
    private final Label rightLabel;

    public ParsedViewerPane(FileComponent file, HexText hex) {
        tree = buildClassTree(file);
        //searchBar = new SearchBar(this);
        hexPane = new HexPane(hex);
        statusLabel = new Label(" ");
        rightLabel = new Label();
        rightLabel.setPrefWidth(10);

        bytesBar = new BytesBar(file.getLength());
        bytesBar.setMaxHeight(statusLabel.getMaxHeight());
        bytesBar.setPrefWidth(200);

        FontUtils.setUIFont(statusLabel);

        //this.setTop(searchBar);
        this.setCenter(buildSplitPane());
        this.setBottom(buildStatusBar());
        this.setRight(rightLabel);

        listenTreeItemSelection();
    }

    private static TreeView<FileComponent> buildClassTree(FileComponent file) {
        file.setExpanded(true);

        TreeView<FileComponent> tree = new TreeView<>(file);
        tree.setMinWidth(200);
        FontUtils.setUIFont(tree);
        return tree;
    }

    private SplitPane buildSplitPane() {
        SplitPane sp = new SplitPane();
        sp.getItems().add(tree);
        sp.getItems().add(hexPane);
        sp.setDividerPositions(0.3, 0.7);
        return sp;
    }

    private BorderPane buildStatusBar() {
        BorderPane statusBar = new BorderPane();
        statusBar.setLeft(statusLabel);
        statusBar.setRight(bytesBar);
        return statusBar;
    }

    private void listenTreeItemSelection() {
        tree.getSelectionModel().getSelectedItems().addListener(this::selectItemAction);
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
        return null;
        //return searchBar;
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
}
