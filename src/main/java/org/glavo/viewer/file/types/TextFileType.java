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
package org.glavo.viewer.file.types;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import kala.compress.utils.Charsets;
import kala.function.CheckedSupplier;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.PlainTextChange;
import org.glavo.viewer.file.CustomFileType;
import org.glavo.viewer.file.FileHandle;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.highlighter.Highlighter;
import org.glavo.viewer.resources.I18N;
import org.glavo.viewer.resources.Resources;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.ui.Schedulers;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.glavo.viewer.util.logging.Logger.LOGGER;

public class TextFileType extends CustomFileType {
    public static final TextFileType TYPE = new TextFileType();

    public static String codeStylesheet = Resources.class.getResource("stylesheet/code.css").toExternalForm();

    public static final ExecutorService highlightPool = Executors.newSingleThreadExecutor(new DaemonThreadFactory("highlighter-common"));
    private static final AtomicInteger count = new AtomicInteger();

    protected Highlighter highlighter;
    protected boolean forceUTF8 = false;
    protected long realtimeHighlightThreshold = 1024 * 1024; // 1 MiB
    protected long sharedThreadPoolThreshold = FileUtils.SMALL_FILE_LIMIT;

    private TextFileType() {
        super("text", Set.of(
                "txt", "md", "asm",
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
                "csv", "inf", "toml", "log"
        ));
    }

    protected TextFileType(String name, Set<String> extensions) {
        super(name, extensions);
    }

    protected TextFileType(String name, Image image, Set<String> extensions) {
        super(name, image, extensions);
    }

    public Highlighter getHighlighter() {
        return highlighter;
    }

    @Override
    public boolean check(VirtualFile file, String ext) {
        if (super.check(file, ext)) {
            return true;
        }

        return switch (file.getFileName()) {
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
                    LOGGER.info(String.format("Start thread %s to highlight file %s", t.getName(), tab.getFile()));
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
                            LOGGER.warning("Highlight task failed", t.getFailure());
                            return Optional.empty();
                        }
                    })
                    .subscribe(h -> area.setStyleSpans(0, h));
        }
    }

    private static final ThreadLocal<UniversalDetector> detector = ThreadLocal.withInitial(UniversalDetector::new);

    @Override
    public FileTab openTab(VirtualFile file) {
        FileTab tab = new FileTab(file, this);

        tab.setContent(new StackPane(new ProgressIndicator()));

        HBox statusBar = new HBox();
        statusBar.setAlignment(Pos.CENTER_RIGHT);
        tab.setStatusBar(statusBar);

        CompletableFuture.supplyAsync(CheckedSupplier.of(() -> {
            record Result(CodeArea area, Charset charset) {
            }
            byte[] bytes;

            file.getContainer().lock();
            FileHandle fileHandle = null;
            try {
                fileHandle = file.getContainer().openFile(file);
                tab.setFileHandle(fileHandle);

                bytes = fileHandle.readAllBytes();
            } catch (Throwable e) {
                tab.setFileHandle(null);
                if (fileHandle != null) {
                    fileHandle.close();
                }
                throw e;
            } finally {
                file.getContainer().unlock();
            }

            Charset charset = detectFileEncoding(bytes);
            CodeArea area = new CodeArea();
            area.getStylesheets().clear();
            area.setParagraphGraphicFactory(LineNumberFactory.get(area));
            area.setEditable(false);
            area.replaceText(new String(bytes, charset));
            applyHighlighter(tab, area);
            return new Result(area, charset);
        })).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                result.area().scrollToPixel(0, 0);
                tab.setContent(new VirtualizedScrollPane<>(result.area()));
                statusBar.getChildren().add(new Label(result.charset().toString()));
            } else {
                LOGGER.warning("Failed to open file", exception);
                tab.setContent(new StackPane(new Label(I18N.getString("failed.openFile"))));
            }
        }, Schedulers.javafx());


        return tab;
    }
}
