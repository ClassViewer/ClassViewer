package org.glavo.viewer.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.util.ImageUtils;

public final class ViewerMenuBar extends MenuBar {

    public class FileMenu extends Menu {
        MenuItem openFileItem = new MenuItem(I18N.getString("fileMenu.openFileItem.text"));
        MenuItem openFolderItem = new MenuItem(I18N.getString("fileMenu.openFolderItem.text"));
        Menu openRecentMenu = new Menu(I18N.getString("fileMenu.openRecentMenu.text"));

        public FileMenu() {
            super(I18N.getString("fileMenu.text"));
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

    public static class WindowMenu extends Menu {
        private final MenuItem newWindowItem = new MenuItem(I18N.getString("windowMenu.newWindowItem.text"));

        public WindowMenu() {
            super(I18N.getString("windowMenu.text"));
            this.setMnemonicParsing(true);

            newWindowItem.setOnAction(event -> new Viewer(new Stage(), false));
            newWindowItem.setMnemonicParsing(true);

            this.getItems().add(newWindowItem);
        }
    }

    public class HelpMenu extends Menu {
        private final MenuItem aboutItem = new MenuItem(I18N.getString("helpMenu.aboutItem.text"));

        public HelpMenu() {
            super(I18N.getString("helpMenu.text"));
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
