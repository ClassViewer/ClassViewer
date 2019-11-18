package org.glavo.viewer.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;

import java.util.ResourceBundle;

import static org.glavo.viewer.util.FontUtils.setUIFont;

public final class ViewerMenuBar extends MenuBar {

    public static final ResourceBundle resource = ResourceBundle.getBundle("org.glavo.viewer.gui.ViewerMenuBarResources");

    public class FileMenu extends Menu {
        private MenuItem openFileItem = new MenuItem(resource.getString("fileMenu.openFileItem.text"));
        private MenuItem openFolderItem = new MenuItem(resource.getString("fileMenu.openFolderItem.text"));
        private Menu openRecentMenu = new Menu(resource.getString("fileMenu.openRecentMenu.text"));

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
        this.fileMenu.openRecentMenu.getItems().clear();
        for (RecentFile file : RecentFiles.Instance.getAll()) {
            MenuItem item = new MenuItem(file.url.toString(), new ImageView(file.type.icon));
            item.setOnAction(event -> viewer.openFile(file.url));
            this.fileMenu.openRecentMenu.getItems().add(item);
        }
    }
}
