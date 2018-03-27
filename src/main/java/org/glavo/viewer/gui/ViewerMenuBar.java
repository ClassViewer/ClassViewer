package org.glavo.viewer.gui;

import de.codecentric.centerdevice.MenuToolkit;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.glavo.viewer.util.ImageUtils;
import org.glavo.viewer.util.PlatformUtils;

import java.util.ResourceBundle;

import static org.glavo.viewer.util.FontUtils.setUIFont;

public final class ViewerMenuBar extends MenuBar {

    public static final ResourceBundle resource = ResourceBundle.getBundle("org.glavo.viewer.gui.ViewerMenuBar");

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

            if (PlatformUtils.isOSX()) {
                openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
                openFolderItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.META_DOWN));
            }

            this.getItems().addAll(openFileItem, openFolderItem, openRecentMenu);
        }
    }

    public class WindowMenu extends Menu {
        private MenuItem newWindowItem = new MenuItem(resource.getString("windowMenu.newWindowItem.text"));
        private MenuItem closeCurrentWindowItem = new MenuItem(resource.getString("windowMenu.closeWindowItem.text"));

        public WindowMenu() {
            super(resource.getString("windowMenu.text"));
            this.setMnemonicParsing(true);

            newWindowItem.setOnAction(event -> new Viewer().start(new Stage()));
            newWindowItem.setMnemonicParsing(true);
            newWindowItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));

            closeCurrentWindowItem.setOnAction(event -> {
                Stage stage = (Stage) getScene().getWindow();
                stage.close();
            });
            closeCurrentWindowItem.setMnemonicParsing(true);
            closeCurrentWindowItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN));


            this.getItems().addAll(newWindowItem, closeCurrentWindowItem);
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

    public class MacDefaultMenu extends Menu {
        private MenuItem aboutItem = new MenuItem(resource.getString("helpMenu.aboutItem.text"));
        private MenuItem preferenceItem = new MenuItem(resource.getString("macDefaultMenu.preference.text"));
        private MenuItem quitItem = new MenuItem(resource.getString("macDefaultMenu.quit.text"));

        MacDefaultMenu() {
            super(resource.getString("helpMenu.text"));
            this.setMnemonicParsing(true);

            aboutItem.setMnemonicParsing(true);
            aboutItem.setOnAction(event -> ViewerAboutDialog.show(viewer));

            preferenceItem.setMnemonicParsing(true);
            preferenceItem.setOnAction(event -> ViewerAboutDialog.show(viewer));

            quitItem.setMnemonicParsing(true);
            quitItem.setOnAction(event -> System.exit(0));
            quitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));

            preferenceItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.META_DOWN));
            this.getItems().addAll(aboutItem, new SeparatorMenuItem(), preferenceItem, new SeparatorMenuItem(), quitItem);
        }
    }

    private Viewer viewer;

    public final FileMenu fileMenu = new FileMenu();
    public final WindowMenu windowMenu = new WindowMenu();
    public final HelpMenu helpMenu = new HelpMenu();
    public final MacDefaultMenu macDefaultMenu = new MacDefaultMenu();

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
