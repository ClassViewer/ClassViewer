package jlink;

import de.undercouch.gradle.tasks.download.Download;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

public final class JlinkPlugin implements Plugin<Project> {
    private static final String JDK_VERSION = "21.0.6+10";

    @Override
    public void apply(Project project) {
        for (JdkPlatform platform : JdkPlatform.PLATFORMS) {
//            TaskProvider<Download> downloadJdkTask = project.getTasks().register("downloadJdk-" + platform, Download.class, task -> {
//                task.setGroup("jlink");
//                task.src(platform.getDownloadUrl(JDK_VERSION));
//            });
//
//
//            project.getTasks().register("jlink-" + platform, JlinkTask.class, task -> {
//                task.setGroup("jlink");
//                task.getPlatform().set(platform);
//            });
//
            // TODO
        }
    }
}
