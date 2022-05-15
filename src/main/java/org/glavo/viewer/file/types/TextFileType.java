package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import kala.compress.utils.Charsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.highlighter.Highlighter;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.TaskUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class TextFileType extends CustomFileType {
    public static final TextFileType TYPE = new TextFileType();

    protected Highlighter highlighter;

    protected TextFileType() {
        super("text");
    }

    protected TextFileType(String name) {
        super(name);
    }

    protected TextFileType(String name, Image image) {
        super(name, image);
    }

    public Highlighter getHighlighter() {
        return highlighter;
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
        if (highlighter != null) {
            area.getStylesheets().add(Stylesheet.getCodeStylesheet());
            area.setStyleSpans(0, getHighlighter().computeHighlighting(area.getText()));
            area.multiPlainChanges()
                    .successionEnds(Duration.ofMillis(100))
                    .retainLatestUntilLater(TaskUtils.highlightPool)
                    .supplyTask(() -> TaskUtils.submitHighlightTask(() -> getHighlighter().computeHighlighting(area.getText())))
                    .awaitLatest(area.multiPlainChanges())
                    .filterMap(t -> {
                        if (t.isSuccess()) {
                            return Optional.of(t.get());
                        } else {
                            LOGGER.log(Level.WARNING, "Highlight task failed", t.getFailure());
                            return Optional.empty();
                        }
                    })
                    .subscribe(h -> area.setStyleSpans(0, h));
        }
    }

    private static final ThreadLocal<UniversalDetector> detector = ThreadLocal.withInitial(UniversalDetector::new);

    @Override
    public FileTab openTab(FileHandle handle) {
        FileTab res = new FileTab(this, handle.getPath());
        res.setContent(new StackPane(new ProgressIndicator()));

        HBox statusBar = new HBox();
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        res.setStatusBar(statusBar);

        Task<Node> task = new Task<Node>() {
            Charset charset;

            @Override
            protected Node call() throws Exception {
                byte[] bytes = handle.readAllBytes();

                UniversalDetector d = detector.get();
                d.reset();
                d.handleData(bytes, 0, Integer.min(8192, bytes.length));
                d.dataEnd();

                charset = Charsets.toCharset(d.getDetectedCharset(), StandardCharsets.UTF_8);
                if (charset == StandardCharsets.US_ASCII) charset = StandardCharsets.UTF_8;

                CodeArea area = new CodeArea();
                area.getStylesheets().clear();
                area.setParagraphGraphicFactory(LineNumberFactory.get(area));
                //area.setEditable(false);
                area.replaceText(new String(bytes, charset));

                applyHighlighter(area);
                area.scrollToPixel(0, 0);

                return new VirtualizedScrollPane<>(area);
            }

            @Override
            protected void succeeded() {
                res.setContent(this.getValue());
                statusBar.getChildren().add(new Label(charset.toString()));
                handle.close();
            }

            @Override
            protected void failed() {
                handle.close();
                throw new UnsupportedOperationException(getException()); // TODO
            }
        };

        TaskUtils.submit(task);

        return res;
    }
}
