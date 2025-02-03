package jlink;

import de.undercouch.gradle.tasks.download.Download;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

public final class JlinkPlugin implements Plugin<Project> {
    private static final Logger LOGGER = Logging.getLogger(JlinkPlugin.class);

    private static final String JDK_VERSION = "21.0.6+10";


    public static String getJdkFileNameBase(JdkPlatform platform) {
        String osName = platform.os.name().toLowerCase(Locale.ROOT);
        String archName = platform.arch.name().toLowerCase(Locale.ROOT);
        String suffix = platform.arch == JdkPlatform.Arch.RISCV64 ? "" : "-full";

        if (platform.arch == JdkPlatform.Arch.X86_64) {
            archName = "amd64";
        }

        return "bellsoft-jdk" + JDK_VERSION + "-" + osName + "-" + archName + suffix;
    }

    public static String getJdkFileName(JdkPlatform platform) {
        String ext = platform.os == JdkPlatform.OS.WINDOWS ? ".zip" : ".tar.gz";
        return getJdkFileNameBase(platform) + ext;
    }

    private static String getJdkDownloadUrl(JdkPlatform platform) {
        return "https://download.bell-sw.com/java/" + JDK_VERSION + "/" + getJdkFileName(platform);
    }

    private static String getJavaFXDownloadUrl(JdkPlatform platform) {
        if (platform.os == JdkPlatform.OS.LINUX && platform.arch == JdkPlatform.Arch.RISCV64) {
            return "https://github.com/Glavo/openjfx-riscv-build/releases/download/21.0.6%2B3/openjfx-21.0.6_linux-riscv64_bin-jmods.zip";
        }

        return null;
    }

    @Override
    public void apply(Project project) {
        for (JdkPlatform platform : JdkPlatform.PLATFORMS) {
            TaskProvider<Download> downloadJdkTask = project.getTasks().register("downloadJdk-" + platform, Download.class, task -> {
                task.setGroup("jlink");
                task.src(getJdkDownloadUrl(platform));
            });

            TaskProvider<Download> downloadJavaFXTask;
            String javafxUrl = getJavaFXDownloadUrl(platform);
            if (javafxUrl != null) {
                downloadJavaFXTask = project.getTasks().register("downloadJavaFX-" + platform, Download.class, task -> {
                    task.setGroup("jlink");
                    task.src(javafxUrl);
                });
            } else {
                downloadJavaFXTask = null;
            }

            project.getTasks().register("jlink-" + platform, JlinkTask.class, task -> {
                task.setGroup("jlink");
                task.dependsOn(downloadJdkTask, project.getTasks().getByName("jar"));
                task.getJdkArchivePath().set(downloadJdkTask.map(it -> it.getOutputFiles().get(0).toPath()));


                if (downloadJavaFXTask != null) {
                    task.dependsOn(downloadJavaFXTask);
                    task.getJavaFXArchivePath().set(downloadJavaFXTask.map(it -> it.getOutputFiles().get(0).toPath()));
                }

                task.getPlatform().set(platform);
                task.getOutputFile().set(project.getLayout().getBuildDirectory()
                        .map(dir -> dir.dir("jlink")
                                .file("ClassViewer-" + project.getVersion() + "-" + platform + "." + platform.os.getArchiveExtension())));
            });
        }
    }
}
