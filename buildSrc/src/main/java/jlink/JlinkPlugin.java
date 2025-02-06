/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Glavo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

        for (JdkPlatform platform : JdkPlatform.values()) {
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
                task.getJdkArchivePath().set(downloadJdkTask.map(it -> it.getOutputFiles().getFirst().toPath()));


                if (downloadJavaFXTask != null) {
                    task.dependsOn(downloadJavaFXTask);
                    task.getJavaFXArchivePath().set(downloadJavaFXTask.map(it -> it.getOutputFiles().getFirst().toPath()));
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
