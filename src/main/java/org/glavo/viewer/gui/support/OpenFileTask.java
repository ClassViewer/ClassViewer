package org.glavo.viewer.gui.support;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Consumer;

import javafx.concurrent.Task;
import org.glavo.viewer.classfile.ClassFileParser;
import org.glavo.viewer.FileComponent;
import org.glavo.viewer.gui.directory.DirectoryTreeLoader;
import org.glavo.viewer.gui.directory.DirectoryTreeNode;
import org.glavo.viewer.gui.jar.JarTreeLoader;
import org.glavo.viewer.gui.jar.JarTreeNode;
import org.glavo.viewer.gui.parsed.HexText;
import org.glavo.viewer.util.Log;
import org.glavo.viewer.util.UrlUtils;

public class OpenFileTask extends Task<OpenFileResult> {

    private static final byte[] classMagicNumber = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
    private static final byte[] binaryChunkSig = {0x1B, 'L', 'u', 'a'};


    private final URL url;

    public OpenFileTask(URL url) {
        this.url = url;
    }

    @Override
    protected OpenFileResult call() throws Exception {
        Log.log("loading " + url + "...");

        FileType fileType = getFileType(url);
        if (fileType == FileType.FOLDER) {
            DirectoryTreeNode rootNode = DirectoryTreeLoader.load(new File(url.toURI()));
            return new OpenFileResult(url, fileType, rootNode);
        }

        if (fileType == FileType.JAVA_JAR) {
            JarTreeNode rootNode = JarTreeLoader.load(new File(url.toURI()));
            return new OpenFileResult(url, fileType, rootNode);
        }

        byte[] data = UrlUtils.readData(url);
        if (fileType == FileType.UNKNOWN) {
            fileType = getFileType(data);
        }

        HexText hex = new HexText(data);
        FileComponent fc = parse(data, fileType);
        fc.setName(UrlUtils.getFileName(url));

        Log.log("finish loading");
        return new OpenFileResult(url, fileType, fc, hex);
    }

    public static FileType getFileType(URL url) {
        String filename = url.toString().toLowerCase();
        if (filename.endsWith("/") || filename.endsWith("\\")) {
            return FileType.FOLDER;
        }

        if (filename.endsWith(".jar")) {
            return FileType.JAVA_JAR;
        }
        if (filename.endsWith(".class")) {
            return FileType.JAVA_CLASS;
        }
        return FileType.UNKNOWN;
    }

    private static FileType getFileType(byte[] data) {
        if (data.length >= 4) {
            byte[] magicNumber = Arrays.copyOf(data, 4);
            if (Arrays.equals(magicNumber, classMagicNumber)) {
                return FileType.JAVA_CLASS;
            }
        }
        return FileType.UNKNOWN;
    }

    private static FileComponent parse(byte[] data, FileType fileType) {
        switch (fileType) {
            case JAVA_CLASS:
                return new ClassFileParser().parse(data);
            default:
                return new FileComponent() {
                }; // todo
        }
    }

    public void setOnSucceeded(Consumer<OpenFileResult> callback) {
        super.setOnSucceeded(
                e -> callback.accept((OpenFileResult) e.getSource().getValue()));
    }

    public void setOnFailed(Consumer<Throwable> callback) {
        super.setOnFailed(event -> {
            Throwable err = event.getSource().getException();
            Log.log(err);

            callback.accept(err);
        });
    }

    public void startInNewThread() {
        new Thread(this).start();
    }

}
