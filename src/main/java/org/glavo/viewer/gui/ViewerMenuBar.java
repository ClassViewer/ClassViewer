package org.glavo.viewer.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.gui.support.RecentFile;
import org.glavo.viewer.gui.support.RecentFiles;

import java.net.URL;
import java.util.function.Consumer;

/**
 * ClassViewer menu bar.
 * <p>
 * File              Window        Help
 * |-Open >         |-New Window  |-About
 * |-Java Class...
 * |-Java Jar...
 * |-Open Recent >
 */
public final class ViewerMenuBar extends MenuBar {

    private final Viewer viewer;

    private Consumer<URL> onOpenFile;
    private Consumer<URL> onOpenFolder;
    private Runnable onNewWindow;

    private FileMenu fileMenu;
    private Menu windowMenu;
    private Menu helpMenu;


    public ViewerMenuBar(Viewer viewer) {
        this.viewer = viewer;
        this.fileMenu = new FileMenu();
        this.windowMenu = new WindowMenu();
        addHelpMenu();

        getMenus().addAll(
                fileMenu,
                windowMenu,
                helpMenu
        );
    }


    private void addHelpMenu() {
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(e -> AboutDialog.showDialog());
        aboutMenuItem.setMnemonicParsing(true);

        Menu helpMenu = new Menu("_Help");
        helpMenu.getItems().add(aboutMenuItem);
        helpMenu.setMnemonicParsing(true);

        getMenus().add(helpMenu);
    }

    public void setOnOpenFile(Consumer<URL> onOpenFile) {
        this.onOpenFile = onOpenFile;
    }

    public void setOnOpenFolder(Consumer<URL> onOpenFolder) {
        this.onOpenFolder = onOpenFolder;
    }

    public void setOnNewWindow(Runnable onNewWindow) {
        this.onNewWindow = onNewWindow;
    }


    public class FileMenu extends Menu {
        MenuItem openMenu;
        MenuItem openFolderMenu;
        Menu openRecentMenu;

        FileMenu() {
            super("_File");
            this.openMenu = createOpenMenu();
            this.openFolderMenu = createOpenFolderMenu();
            this.openRecentMenu = createOpenRecentMenu();

            getItems().addAll(
                    openMenu,
                    openFolderMenu,
                    openRecentMenu
            );
            setMnemonicParsing(true);
        }

        private MenuItem createOpenMenu() {
            MenuItem openMenu = new MenuItem("_Open...", new ImageView(ImageUtils.openFileImage));
            openMenu.setOnAction(e -> onOpenFile.accept(null));
            openMenu.setMnemonicParsing(true);
            return openMenu;
        }

        private MenuItem createOpenFolderMenu() {
            MenuItem openMenu = new MenuItem("Open Folder", new ImageView(ImageUtils.openFolderImage));
            openMenu.setOnAction(e -> onOpenFolder.accept(null));
            openMenu.setMnemonicParsing(true);
            return openMenu;
        }

        private Menu createOpenRecentMenu() {
            Menu recentMenu = new Menu("Open _Recent", ImageUtils.createImageView("/icons/clock.png"));
            recentMenu.setMnemonicParsing(true);
            this.openRecentMenu = recentMenu;
            updateRecentFiles();
            return recentMenu;
        }

        public void updateRecentFiles() {
            openRecentMenu.getItems().clear();
            for (RecentFile rf : RecentFiles.INSTANCE.getAll()) {
                ImageView icon = new ImageView(rf.type.icon);
                MenuItem menuItem = new MenuItem(rf.url.toString(), icon);
                menuItem.setOnAction(e -> onOpenFile.accept(rf.url));
                openRecentMenu.getItems().add(menuItem);
            }
        }

    }

    public class WindowMenu extends Menu {
        MenuItem newWindowMenu;

        public WindowMenu() {
            super("_Window");
            setOnAction(e -> onNewWindow.run());

        }
    }
}
