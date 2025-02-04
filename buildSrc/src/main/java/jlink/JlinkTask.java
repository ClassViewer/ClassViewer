package jlink;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.file.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

public abstract class JlinkTask extends DefaultTask {
    @Input
    public abstract Property<JdkPlatform> getPlatform();

    @InputFile
    public abstract Property<Path> getJdkArchivePath();

    @InputFile
    @Optional
    public abstract Property<Path> getJavaFXArchivePath();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    private static final String[] REQUIRED_MODULES = {
            "java.base",
            "java.datatransfer",
            "java.xml",
            "java.prefs",
            "java.desktop",
            "jdk.unsupported",
            "jdk.zipfs",

            // javafx
            "javafx.base",
            "javafx.graphics",
            "javafx.controls"
    };


    private static ArchiveInputStream<?> openArchive(Path path) throws IOException {
        if (path == null) {
            return null;
        }

        String fileName = path.getFileName().toString();

        if (fileName.endsWith(".tar.gz")) {
            return new TarArchiveInputStream(new GZIPInputStream(Files.newInputStream(path)));
        } else if (fileName.endsWith(".zip")) {
            return new ZipArchiveInputStream(Files.newInputStream(path));
        } else {
            throw new AssertionError("Unsupported file format: " + path);
        }
    }

    private static void copyJMods(Set<String> modules, Path targetDir, ArchiveInputStream<?> archive) throws IOException {
        ArchiveEntry entry;
        while ((entry = archive.getNextEntry()) != null) {
            if (entry.getName().endsWith(".jmod")) {
                for (String module : modules) {
                    if (entry.getName().endsWith("/" + module + ".jmod")) {
                        modules.remove(module);
                        Files.copy(archive, targetDir.resolve(module + ".jmod"));
                        break;
                    }
                }
            }
        }
    }

    @TaskAction
    public void run() throws Exception {
        if (JavaVersion.current() != JavaVersion.VERSION_21) {
            throw new GradleException("The jlink task requires JDK 21");
        }

        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")) {
            throw new GradleException("The jlink task does not work on Windows");
        }

        String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            throw new GradleException("java.home is empty");
        }

        Path jlink = Path.of(javaHome, "bin", "jlink");
        if (!Files.exists(jlink)) {
            throw new GradleException(jlink + " does not exist");
        }

        Path jdkPath = getJdkArchivePath().get();
        Path javafxPath = getJavaFXArchivePath().getOrNull();

        Jar jarTask = (Jar) getProject().getTasks().getByName("jar");
        Path viewerJar = jarTask.getArchiveFile().get().getAsFile().toPath();

        // run

        Path tempDirectory = Files.createTempDirectory("viewer-jlink-" + getPlatform().get() + "-");

        try {
            try (ArchiveInputStream<?> jdkArchive = openArchive(jdkPath);
                 ArchiveInputStream<?> javafxArchive = openArchive(javafxPath)) {

                Set<String> modules = new HashSet<>(Arrays.asList(REQUIRED_MODULES));
                copyJMods(modules, tempDirectory, jdkArchive);
                if (javafxArchive != null) {
                    copyJMods(modules, tempDirectory, javafxArchive);
                }

                if (!modules.isEmpty()) {
                    throw new GradleException("These modules were not found: " + String.join(", ", modules));
                }
            }

            Files.copy(viewerJar, tempDirectory.resolve(viewerJar.getFileName()));

            String outputName = "ClassViewer-" + getProject().getVersion() + "-" + getPlatform().get();
            Path outputDir = tempDirectory.resolve(outputName);
            Path outputFile = getOutputFile().getAsFile().get().toPath();

            getProject().getProviders().exec(spec -> {
                spec.setCommandLine(jlink,
                        "--module-path", tempDirectory,
                        "--add-modules", "ALL-MODULE-PATH",
                        "--no-header-files", "--no-man-pages",
                        "--output", outputDir
                );
            }).getResult().get().assertNormalExitValue();

            if (outputFile.getFileName().toString().endsWith(".tar.gz")) {
                getProject().getProviders().exec(spec -> {
                    spec.setWorkingDir(tempDirectory);
                    spec.setCommandLine("tar", "-czf", outputFile, outputName);
                }).getResult().get().assertNormalExitValue();
            } else if (outputFile.getFileName().toString().endsWith(".zip")) {
                getProject().getProviders().exec(spec -> {
                    spec.setWorkingDir(tempDirectory);
                    spec.setCommandLine("zip", "-r", outputFile, outputName);
                }).getResult().get().assertNormalExitValue();
            } else {
                throw new AssertionError("Unsupported output format: " + outputFile);
            }

        } finally {
            PathUtils.deleteDirectory(tempDirectory);
        }
    }
}
