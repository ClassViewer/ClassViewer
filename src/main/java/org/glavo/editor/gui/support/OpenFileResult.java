package org.glavo.editor.gui.support;

import org.glavo.editor.FileComponent;
import org.glavo.editor.gui.jar.JarTreeNode;
import org.glavo.editor.gui.parsed.HexText;

import java.net.URL;

public class OpenFileResult {

    public final URL url;
    public final FileType fileType;
    public final JarTreeNode jarRootNode;
    public final FileComponent fileRootNode;
    public final HexText hexText;

    public OpenFileResult(URL url, FileType fileType,
                          JarTreeNode jarTreeNode) {
        this.url = url;
        this.fileType = fileType;
        this.jarRootNode = jarTreeNode;
        this.fileRootNode = null;
        this.hexText = null;
    }

    public OpenFileResult(URL url, FileType fileType,
                          FileComponent fileRootNode, HexText hexText) {
        this.url = url;
        this.fileType = fileType;
        this.jarRootNode = null;
        this.fileRootNode = fileRootNode;
        this.hexText = hexText;
    }

}
