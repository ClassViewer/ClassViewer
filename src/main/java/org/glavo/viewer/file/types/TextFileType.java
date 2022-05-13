package org.glavo.viewer.file.types;

import javafx.scene.image.Image;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileType;
import org.glavo.viewer.ui.FileTab;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TextFileType extends CustomFileType {
    public static final TextFileType TYPE = new TextFileType();

    protected TextFileType() {
        super("text");
    }

    protected TextFileType(String name) {
        super(name);
    }

    protected TextFileType(String name, Image image) {
        super(name, image);
    }

    @Override
    public boolean check(FilePath path) {
        switch (path.getFileNameExtension()) {
            case "txt":
            case "md":

            case "asm":
            case "c":
            case "cc":
            case "cpp":
            case "cxx":
            case "cs":
            case "clj":
            case "f":
            case "for":
            case "f90":
            case "f95":
            case "fs":
            case "go":
            case "gradle":
            case "groovy":
            case "h":
            case "hpp":
            case "hs":
            case "java":
            case "js":
            case "jl":
            case "kt":
            case "kts":
            case "m":
            case "mm":
            case "ml":
            case "mli":
            case "py":
            case "pl":
            case "ruby":
            case "rs":
            case "swift":
            case "vala":
            case "vapi":
            case "zig":


            case "sh":
            case "bat":
            case "ps1":

            case "csv":
            case "inf":
            case "toml":
            case "log":
                return true;
        }

        switch (path.getFileName()) {
            case ".bashrc":
            case ".zshrc":
            case ".gitignore":
            case "gradlew":
            case "LICENSE":
                return true;
        }

        return false;
    }

    @Override
    public FileTab openTab(FilePath path) {
        return super.openTab(path);
    }
}
