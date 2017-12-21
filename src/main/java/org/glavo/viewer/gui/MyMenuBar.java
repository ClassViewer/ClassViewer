package org.glavo.viewer.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.ImageUtils;
import org.glavo.viewer.gui.support.RecentFile;
import org.glavo.viewer.gui.support.RecentFiles;

import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Classpy menu bar.
 * <p>
 * File              Window        Help
 * |-Open >         |-New Window  |-About
 * |-Java Class...
 * |-Java Jar...
 * |-Open Recent >
 */
public final class MyMenuBar extends MenuBar {

    BiConsumer<FileType, URL> onOpenFileWithType;
    Consumer<URL> onOpenFile;
    Consumer<URL> onOpenFolder;
    Runnable onNewWindow;

    public MyMenuBar() {
        addFileMenu();
        addWindowMenu();
        addHelpMenu();
    }

    private void addFileMenu() {
        Menu fileMenu = new Menu("_File");
        fileMenu.getItems().addAll(
                createOpenMenu(),
                createOpenFolderMenu(),
                createRecentMenu()
        );
        fileMenu.setMnemonicParsing(true);
        getMenus().add(fileMenu);
    }

    private MenuItem createOpenMenu() {
        MenuItem openMenu = new MenuItem("_Open...", new ImageView(ImageUtils.openFileImage));
        openMenu.setOnAction(e -> onOpenFile.accept(null));
        openMenu.setMnemonicParsing(true);
        return openMenu;
    }

    private MenuItem createOpenFolderMenu() {
        MenuItem openMenu = new MenuItem("Open Folder", new ImageView(ImageUtils.openFoldermage));
        openMenu.setOnAction(e -> onOpenFolder.accept(null));
        openMenu.setMnemonicParsing(true);
        return openMenu;
    }

    private Menu createRecentMenu() {
        Menu recentMenu = new Menu("Open _Recent", ImageUtils.createImageView("/icons/clock.png"));
        for (RecentFile rf : RecentFiles.INSTANCE.getAll()) {
            ImageView icon = new ImageView(rf.type.icon);
            MenuItem menuItem = new MenuItem(rf.url.toString(), icon);
            menuItem.setOnAction(e -> onOpenFileWithType.accept(rf.type, rf.url));
            recentMenu.getItems().add(menuItem);
        }
        recentMenu.setMnemonicParsing(true);
        return recentMenu;
    }

    private void addWindowMenu() {
        MenuItem newWinMenuItem = new MenuItem("New Window");
        newWinMenuItem.setOnAction(e -> onNewWindow.run());

        Menu winMenu = new Menu("_Window");
        winMenu.getItems().add(newWinMenuItem);
        winMenu.setMnemonicParsing(true);

        getMenus().add(winMenu);
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

    public void setOnOpenFileWithType(BiConsumer<FileType, URL> onOpenFileWithType) {
        this.onOpenFileWithType = onOpenFileWithType;
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

    public void updateRecentFiles() {
        Menu fileMenu = getMenus().get(0);
        fileMenu.getItems().set(2, createRecentMenu());
    }
}
