/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file2.types;

import javafx.scene.image.Image;
import kala.compress.utils.Charsets;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.glavo.viewer.file2.CustomFileType;
import org.glavo.viewer.file2.VirtualFile;
import org.glavo.viewer.highlighter.Highlighter;
import org.glavo.viewer.resources.Resources;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.FileTab2;
import org.glavo.viewer.util.DaemonThreadFactory;
import org.glavo.viewer.util.FileUtils;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static org.glavo.viewer.util.Logging.LOGGER;

public class TextFileType extends CustomFileType {
    public static final TextFileType TYPE = new TextFileType();

    public static String codeStylesheet = Resources.class.getResource("stylesheet/code.css").toExternalForm();

    public static final ExecutorService highlightPool = Executors.newSingleThreadExecutor(new DaemonThreadFactory("highlighter-common"));
    private static final AtomicInteger count = new AtomicInteger();

    protected Highlighter highlighter;
    protected boolean forceUTF8 = false;
    protected long realtimeHighlightThreshold = 1024 * 1024; // 1 MiB
    protected long sharedThreadPoolThreshold = FileUtils.SMALL_FILE_LIMIT;

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
    public boolean check(VirtualFile path) {
        switch (path.getFileName()) { // TODO: ext
            case "txt", "md", "asm",
                    "c", "cc", "cpp", "cxx", "cs", "clj",
                    "f", "for", "f90", "f95", "fs",
                    "go", "gradle", "groovy",
                    "h", "hpp", "hs",
                    "java", "js", "jl",
                    "kt", "kts",
                    "m", "mm", "ml", "mli",
                    "py", "pl",
                    "ruby", "rs",
                    "swift",
                    "vala", "vapi",
                    "zig",
                    "sh", "bat", "ps1",
                    "csv", "inf", "toml", "log" -> {
                return true;
            }
        }

        return switch (path.getFileName()) {
            case ".bashrc", ".zshrc", ".gitignore", "gradlew", "LICENSE" -> true;
            default -> false;
        };
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
            area.getStylesheets().add(codeStylesheet);
            area.setStyleSpans(0, getHighlighter().computeHighlighting(area.getText()));

            int textLength = area.getText().length();
            EventStream<List<PlainTextChange>> multiPlainChanges = area.multiPlainChanges();

            ExecutorService pool;
            if (textLength <= realtimeHighlightThreshold) {
                pool = TextFileType.highlightPool;
            } else {
                pool = Executors.newSingleThreadExecutor(r -> {
                    Thread t = new Thread(r, "highlighter-" + count.getAndIncrement());
                    t.setDaemon(true);
                    LOGGER.info(String.format("Start thread %s to highlight file %s", t.getName(), tab.getPath()));
                    return t;
                });

                tab.setOnClosed(event -> pool.shutdown());
            }

            EventStream<List<PlainTextChange>> stream = multiPlainChanges;
            if (textLength >= realtimeHighlightThreshold) {
                stream = stream.successionEnds(Duration.ofMillis(250));
            }

            stream
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
        }
    }

    private static final ThreadLocal<UniversalDetector> detector = ThreadLocal.withInitial(UniversalDetector::new);

    @Override
    public FileTab2 openTab(VirtualFile file) {
//        FileTab res = new FileTab(this, handle.getPath());
//        res.setContent(new StackPane(new ProgressIndicator()));
//
//        HBox statusBar = new HBox();
//        statusBar.setAlignment(Pos.CENTER_RIGHT);
//        res.setStatusBar(statusBar);
//
//        Task<CodeArea> task = new Task<>() {
//            Charset charset;
//
//            @Override
//            protected CodeArea call() throws Exception {
//                byte[] bytes = handle.readAllBytes();
//
//                charset = detectFileEncoding(bytes);
//
//                CodeArea area = new CodeArea();
//                area.getStylesheets().clear();
//                area.setParagraphGraphicFactory(LineNumberFactory.get(area));
//                //area.setEditable(false);
//                area.replaceText(new String(bytes, charset));
//                applyHighlighter(res, area);
//                area.scrollToPixel(0, 0);
//
//                return area;
//            }
//
//            @Override
//            protected void succeeded() {
//                res.setContent(new VirtualizedScrollPane<>(this.getValue()));
//                statusBar.getChildren().add(new Label(charset.toString()));
//                handle.close();
//            }
//
//            @Override
//            protected void failed() {
//                LOGGER.log(Level.WARNING, "Failed to open file", getException());
//                res.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
//                handle.close();
//            }
//        };
//
//        TaskUtils.submit(task);
//
//        return res;
        throw new UnsupportedOperationException("TODO");
    }
}
