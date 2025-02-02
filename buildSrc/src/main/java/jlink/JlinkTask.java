package jlink;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class JlinkTask extends DefaultTask {
    @Input
    public abstract Property<JdkPlatform> getPlatform();
}
