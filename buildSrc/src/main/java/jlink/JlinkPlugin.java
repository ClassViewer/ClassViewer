package jlink;

import de.undercouch.gradle.tasks.download.Download;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class JlinkPlugin implements Plugin<Project> {
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
        String ext = platform.os == JdkPlatform.OS.LINUX ? ".tar.gz" : ".zip";
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
        List<TaskProvider<?>> jlinkTasks = new ArrayList<>();

        Provider<Directory> downloadDir = project.getLayout().getBuildDirectory().dir("download");


        for (JdkPlatform platform : JdkPlatform.PLATFORMS) {
            TaskProvider<Download> downloadJdkTask = project.getTasks().register("downloadJdk-" + platform, Download.class, task -> {
                task.setGroup("jlink");
                task.src(getJdkDownloadUrl(platform));
                task.dest(downloadDir);
                task.overwrite(false);
            });

            TaskProvider<Download> downloadJavaFXTask;
            String javafxUrl = getJavaFXDownloadUrl(platform);
            if (javafxUrl != null) {
                downloadJavaFXTask = project.getTasks().register("downloadJavaFX-" + platform, Download.class, task -> {
                    task.setGroup("jlink");
                    task.src(javafxUrl);
                    task.dest(downloadDir);
                    task.overwrite(false);
                });
            } else {
                downloadJavaFXTask = null;
            }

            jlinkTasks.add(project.getTasks().register("jlink-" + platform, JlinkTask.class, task -> {
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
            }));
        }

        project.getTasks().register("jlink", task -> {
            task.setGroup("jlink");
            task.dependsOn(jlinkTasks.toArray());
        });
    }
}
