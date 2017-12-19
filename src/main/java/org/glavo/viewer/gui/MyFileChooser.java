package org.glavo.viewer.gui;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.RecentFiles;

import java.io.File;

public class MyFileChooser {

    private static FileChooser fileChooser;

    public static File showFileChooser(Stage stage, FileType ft) {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
        }

        File lastOpenFile = RecentFiles.INSTANCE.getLastOpenFile(ft);
        if (lastOpenFile != null && lastOpenFile.getParentFile().isDirectory()) {
            fileChooser.setInitialDirectory(lastOpenFile.getParentFile());
        }

        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(ft.filter);

        return fileChooser.showOpenDialog(stage);
    }

}
