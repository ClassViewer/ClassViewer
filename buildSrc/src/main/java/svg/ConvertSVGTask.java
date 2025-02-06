package svg;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.file.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class ConvertSVGTask extends DefaultTask {
    @InputDirectory
    public abstract DirectoryProperty getInputDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void run() throws IOException {
        System.setProperty("java.awt.headless", "true");

        Path inputDir = getInputDirectory().getAsFile().get().toPath().toAbsolutePath().normalize();
        Path outputDir = getOutputDirectory().getAsFile().get().toPath().toAbsolutePath().normalize();

        if (Files.exists(outputDir)) {
            PathUtils.deleteDirectory(outputDir);
        }
        Files.createDirectories(outputDir);

        if (!Files.exists(inputDir)) {
            return;
        }

        if (!Files.isDirectory(inputDir)) {
            throw new IllegalStateException("Input directory " + inputDir + " is not a directory");
        }

        final var transcoder = new PNGTranscoder();

        Files.walkFileTree(inputDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(@NotNull Path file, BasicFileAttributes attrs) throws IOException {
                String sourceFileName = file.getFileName().toString();

                if (sourceFileName.endsWith(".svg")) {
                    Path parent = outputDir.resolve(inputDir.relativize(file)).getParent();
                    Files.createDirectories(parent);


                    var input = new TranscoderInput(file.toUri().toString());

                    String baseName = sourceFileName.substring(0, sourceFileName.length() - 4);

                    try (var out = Files.newOutputStream(parent.resolve(baseName + ".png"))) {
                        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 16f);
                        transcoder.transcode(input, new TranscoderOutput(out));
                    } catch (TranscoderException e) {
                        throw new IOException(e);
                    }

                    try (var out = Files.newOutputStream(parent.resolve(baseName + "@2x.png"))) {
                        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 32f);
                        transcoder.transcode(input, new TranscoderOutput(out));
                    } catch (TranscoderException e) {
                        throw new IOException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

    }
}
