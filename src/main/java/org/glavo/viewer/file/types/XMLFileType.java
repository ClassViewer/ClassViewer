package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class XMLFileType extends TextFileType {

    public static final XMLFileType TYPE = new XMLFileType();

    private XMLFileType() {
        super("xml");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().endsWith(".xml");
    }
}
