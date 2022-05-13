package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.FileStub;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.TaskUtils;

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

    protected void applyHighlighter(CodeArea area) {

    }

    @Override
    public FileTab openTab(FileStub stub) {
        FileTab res = new FileTab(this, stub.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));

        Task<Node> task = new Task<Node>() {
            @Override
            protected Node call() throws Exception {
                CodeArea area = new CodeArea();
                area.getStylesheets().clear();
                area.setParagraphGraphicFactory(LineNumberFactory.get(area));
                //area.setEditable(false);
                applyHighlighter(area);

                area.replaceText(new String(stub.readAllBytes()));
                area.scrollToPixel(0, 0);

                return new VirtualizedScrollPane<>(area);
            }

            @Override
            protected void succeeded() {
                res.setContent(this.getValue());
            }

            @Override
            protected void failed() {
                throw new UnsupportedOperationException(getException()); // TODO
            }
        };

        TaskUtils.submit(task);

        return res;
    }
}
