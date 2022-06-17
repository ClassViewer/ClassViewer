package org.glavo.viewer.file.types.xml;

import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.TextFileType;

public class XMLFileType extends TextFileType {

    public static final XMLFileType TYPE = new XMLFileType();

    private XMLFileType() {
        super("xml");
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("xml") || path.getFileNameExtension().equals("pom");
    }
}
