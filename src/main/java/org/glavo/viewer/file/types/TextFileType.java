package org.glavo.viewer.file.types;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import kala.compress.utils.Charsets;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.PlainTextChange;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.highlighter.Highlighter;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.DaemonThreadFactory;
import org.glavo.viewer.util.FileUtils;
import org.glavo.viewer.util.Stylesheet;
import org.glavo.viewer.util.TaskUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.reactfx.EventStream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class TextFileType extends CustomFileType {
    public static final TextFileType TYPE = new TextFileType();

    public static final ExecutorService highlightPool = Executors.newSingleThreadExecutor(new DaemonThreadFactory("highlight-common"));

    protected Highlighter highlighter;
    protected boolean forceUTF8 = false;
    protected int realtimeHighlightThreshold = (int) FileUtils.SMALL_FILE_LIMIT;

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

    protected Charset detectFileEncoding(byte[] bytes) {
        if (forceUTF8) {
            return StandardCharsets.UTF_8;
        }
        UniversalDetector d = detector.get();
        d.reset();
        d.handleData(bytes, 0, Integer.min(8192, bytes.length));
        d.dataEnd();

        Charset charset = Charsets.toCharset(d.getDetectedCharset(), StandardCharsets.UTF_8);
        return charset == StandardCharsets.US_ASCII ? StandardCharsets.UTF_8 : charset;
    }

    protected void applyHighlighter(FileTab tab, CodeArea area) {
        if (highlighter != null) {
            area.getStylesheets().add(Stylesheet.getCodeStylesheet());
            area.setStyleSpans(0, getHighlighter().computeHighlighting(area.getText()));
            EventStream<List<PlainTextChange>> multiPlainChanges = area.multiPlainChanges();

            // Real-time highlighting for small files;
            // If the file is too large, highlight it without modification within a second,
            // and use a separate thread pool to prevent the highlighting thread from blocking.
            if (area.getText().length() <= realtimeHighlightThreshold) {
                multiPlainChanges
                        .retainLatestUntilLater(highlightPool)
                        .supplyTask(() -> TaskUtils.submit(highlightPool, () -> getHighlighter().computeHighlighting(area.getText())))
                        .awaitLatest(multiPlainChanges)
                        .filterMap(t -> {
                            if (t.isSuccess()) {
                                return Optional.of(t.get());
                            } else {
                                LOGGER.log(Level.WARNING, "Highlight task failed", t.getFailure());
                                return Optional.empty();
                            }
                        })
                        .subscribe(h -> area.setStyleSpans(0, h));
            } else {
                ExecutorService pool = Executors.newSingleThreadExecutor(new DaemonThreadFactory("Highlight[" + tab.getPath() + "]"));
                multiPlainChanges
                        .successionEnds(Duration.ofSeconds(1))
                        .retainLatestUntilLater(pool)
                        .supplyTask(() -> TaskUtils.submit(pool, () -> getHighlighter().computeHighlighting(area.getText())))
                        .awaitLatest(multiPlainChanges)
                        .filterMap(t -> {
                            if (t.isSuccess()) {
                                return Optional.of(t.get());
                            } else {
                                LOGGER.log(Level.WARNING, "Highlight task failed", t.getFailure());
                                return Optional.empty();
                            }
                        })
                        .subscribe(h -> area.setStyleSpans(0, h));

                tab.setOnClosed(event -> pool.shutdown());
            }
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

        Task<CodeArea> task = new Task<CodeArea>() {
            Charset charset;

            @Override
            protected CodeArea call() throws Exception {
                byte[] bytes = handle.readAllBytes();

                charset = detectFileEncoding(bytes);

                CodeArea area = new CodeArea();
                area.getStylesheets().clear();
                area.setParagraphGraphicFactory(LineNumberFactory.get(area));
                //area.setEditable(false);
                area.replaceText(new String(bytes, charset));
                applyHighlighter(res, area);
                area.scrollToPixel(0, 0);

                return area;
            }

            @Override
            protected void succeeded() {
                res.setContent(new VirtualizedScrollPane<>(this.getValue()));
                statusBar.getChildren().add(new Label(charset.toString()));
                handle.close();
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.WARNING, "Failed to open file", getException());
                res.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
                handle.close();
            }
        };

        TaskUtils.submit(task);

        return res;
    }
}
