package org.glavo.viewer.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.UrlUtils;

import static org.glavo.viewer.util.FontUtils.setUIFont;

public final class ViewerMenuBar extends MenuBar {
    public class FileMenu extends Menu {
        private MenuItem openFile = new MenuItem("Open...");
        private MenuItem openFolder = new MenuItem("Open Folder");
        private Menu openRecent = new Menu("Open Recent");

        public FileMenu() {
            super("_File");
            this.setMnemonicParsing(true);

            openFile.setGraphic(new ImageView(ImageUtils.openFileImage));
            openFolder.setGraphic(new ImageView(ImageUtils.openFolderImage));

            openFile.setOnAction(event -> viewer.openFile());

            this.getItems().addAll(openFile, openFolder, openRecent);
        }
    }

    public class WindowMenu extends Menu {
        private MenuItem newWindow = new MenuItem("New Window");

        public WindowMenu() {
            super("_Window");
            this.setMnemonicParsing(true);

            newWindow.setOnAction(event -> new Viewer().start(new Stage()));
            this.getItems().add(newWindow);
        }
    }

    public class HelpMenu extends Menu {
        public HelpMenu() {
            super("_Help");
            this.setMnemonicParsing(true);
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
        this.fileMenu.openRecent.getItems().clear();
        for (RecentFile file : RecentFiles.Instance.getAll()) {
            MenuItem item = new MenuItem(file.url.toString(), new ImageView(file.type.icon));
            item.setOnAction(event -> viewer.openFile(file.url));
            this.fileMenu.openRecent.getItems().add(item);
        }
    }
}
