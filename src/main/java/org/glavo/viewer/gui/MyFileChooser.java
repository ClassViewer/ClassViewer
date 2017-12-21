package org.glavo.viewer.gui;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.glavo.viewer.gui.support.FileType;
import org.glavo.viewer.gui.support.RecentFiles;

import java.io.File;

public final class MyFileChooser {

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

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

    public static File showFileChooser(Stage stage) {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");
        }

        File lastOpenFile = RecentFiles.INSTANCE.getLastOpenFile();
        if (lastOpenFile != null && lastOpenFile.getParentFile().isDirectory()) {
            fileChooser.setInitialDirectory(lastOpenFile.getParentFile());
        }

        fileChooser.getExtensionFilters().clear();

        fileChooser.getExtensionFilters().add(FileType.allFiles);
        for (FileType type : FileType.fileTypes) {
            fileChooser.getExtensionFilters().add(type.filter);
        }

        return fileChooser.showOpenDialog(stage);
    }

    public static File showDirectoryChooser(Stage stage) {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open folder");
        }
        File lastOpenFile = RecentFiles.INSTANCE.getLastOpenFile();
        if (lastOpenFile != null && lastOpenFile.getParentFile().isDirectory()) {
            directoryChooser.setInitialDirectory(lastOpenFile.getParentFile());
        }
        return directoryChooser.showDialog(stage);
    }

}
