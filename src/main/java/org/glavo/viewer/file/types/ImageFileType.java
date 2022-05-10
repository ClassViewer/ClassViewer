package org.glavo.viewer.file.types;

import org.glavo.viewer.file.FilePath;

public class ImageFileType extends CustomFileType {

    public static final ImageFileType TYPE = new ImageFileType();

    private ImageFileType() {
        super("image");
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "bmp":
            case "gif":
            case "png":
            case "jpg":
            case "jpeg":
            case "webp":
                return true;
        }

        return false;
    }
}
