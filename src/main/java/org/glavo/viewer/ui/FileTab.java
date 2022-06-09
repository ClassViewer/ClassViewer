package org.glavo.viewer.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.glavo.viewer.file.LocalFilePath;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.resources.I18N;

public class FileTab extends Tab {
    private final FileType type;
    private final LocalFilePath path;

    private final ObjectProperty<Node> sideBar = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    public FileTab(FileType type, LocalFilePath path) {
        this.type = type;
        this.path = path;

        this.setGraphic(new ImageView(type.getImage()));
        this.setText(path.getFileName());
        this.setTooltip(new Tooltip(path.toString()));
        this.setContextMenu(new TabMenu());
    }

    public FileType getType() {
        return type;
    }

    public LocalFilePath getPath() {
        return path;
    }

    public ObjectProperty<Node> sideBarProperty() {
        return sideBar;
    }

    public Node getSideBar() {
        return sideBar.get();
    }

    public void setSideBar(Node sideBar) {
        this.sideBar.set(sideBar);
    }

    public ObjectProperty<Node> statusBarProperty() {
        return statusBar;
    }

    public Node getStatusBar() {
        return statusBar.get();
    }

    public void setStatusBar(Node statusBar) {
        this.statusBar.set(statusBar);
    }

    public final class TabMenu extends ContextMenu {
        public TabMenu() {
            MenuItem close = new MenuItem(I18N.getString("filesTabPane.menu.close"));
            close.setOnAction(event -> getTabPane().getTabs().remove(FileTab.this));

            MenuItem closeOtherTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeOtherTabs"));
            closeOtherTabs.setOnAction(event -> getTabPane().getTabs().removeIf(it -> it != FileTab.this));

            MenuItem closeAllTabs = new MenuItem(I18N.getString("filesTabPane.menu.closeAllTabs"));
            closeAllTabs.setOnAction(event -> getTabPane().getTabs().clear());

            MenuItem closeTabsToTheLeft = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheLeft"));
            closeTabsToTheLeft.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                int idx = tabPane.getTabs().indexOf(FileTab.this);
                if (idx > 0) {
                    tabPane.getTabs().remove(0, idx);
                }

                getTabPane().getSelectionModel().select(FileTab.this);
            });

            MenuItem closeTabsToTheRight = new MenuItem(I18N.getString("filesTabPane.menu.closeTabsToTheRight"));
            closeTabsToTheRight.setOnAction(event -> {
                TabPane tabPane = getTabPane();
                int idx = tabPane.getTabs().indexOf(FileTab.this);
                if (idx >= 0 && idx < tabPane.getTabs().size() - 1) {
                    tabPane.getTabs().remove(idx + 1, tabPane.getTabs().size());
                }
                getTabPane().getSelectionModel().select(FileTab.this);
            });

            this.getItems().setAll(close, closeOtherTabs, closeAllTabs, closeTabsToTheLeft, closeTabsToTheRight);
        }
    }
}
