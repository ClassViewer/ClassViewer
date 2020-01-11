package org.glavo.viewer.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;

import static org.glavo.viewer.gui.Viewer.resource;

import static org.glavo.viewer.util.FontUtils.setUIFont;

public final class ViewerMenuBar extends MenuBar {


    public class FileMenu extends Menu {
        MenuItem openFileItem = new MenuItem(resource.getString("fileMenu.openFileItem.text"));
        MenuItem openFolderItem = new MenuItem(resource.getString("fileMenu.openFolderItem.text"));
        Menu openRecentMenu = new Menu(resource.getString("fileMenu.openRecentMenu.text"));

        public FileMenu() {
            super(resource.getString("fileMenu.text"));
            this.setMnemonicParsing(true);

            openFileItem.setGraphic(new ImageView(ImageUtils.openFileImage));
            openFolderItem.setGraphic(new ImageView(ImageUtils.openFolderImage));

            openFileItem.setOnAction(event -> viewer.openFile());
            openFolderItem.setOnAction(event -> viewer.openFolder());

            openFileItem.setMnemonicParsing(true);
            openFolderItem.setMnemonicParsing(true);
            openRecentMenu.setMnemonicParsing(true);

            openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
            openFolderItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));

            this.getItems().addAll(openFileItem, openFolderItem, openRecentMenu);
        }
    }

    public class WindowMenu extends Menu {
        private MenuItem newWindowItem = new MenuItem(resource.getString("windowMenu.newWindowItem.text"));

        public WindowMenu() {
            super(resource.getString("windowMenu.text"));
            this.setMnemonicParsing(true);

            newWindowItem.setOnAction(event -> new Viewer().start(new Stage()));
            newWindowItem.setMnemonicParsing(true);

            this.getItems().add(newWindowItem);
        }
    }

    public class HelpMenu extends Menu {
        private MenuItem aboutItem = new MenuItem(resource.getString("helpMenu.aboutItem.text"));

        public HelpMenu() {
            super(resource.getString("helpMenu.text"));
            this.setMnemonicParsing(true);

            aboutItem.setMnemonicParsing(true);
            aboutItem.setOnAction(event -> ViewerAboutDialog.show(viewer));

            this.getItems().add(aboutItem);
        }
    }

    private Viewer viewer;

    public final FileMenu fileMenu = new FileMenu();
    public final WindowMenu windowMenu = new WindowMenu();
    public final HelpMenu helpMenu = new HelpMenu();

    public ViewerMenuBar(Viewer viewer) {
        this.viewer = viewer;
        this.getMenus().addAll(fileMenu, windowMenu, helpMenu);
        updateRecentFiles();

        setUIFont(this);
    }

    public void updateRecentFiles() {
        RecentFiles.init();
        this.fileMenu.openRecentMenu.getItems().clear();
        for (RecentFile file : RecentFiles.Instance.getAll()) {
            MenuItem item = new MenuItem(file.url.toString(), new ImageView(file.type.icon));
            item.setOnAction(event -> viewer.openFile(file.url));
            this.fileMenu.openRecentMenu.getItems().add(item);
        }
    }
}
