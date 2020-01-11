package org.glavo.viewer.gui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class ViewerResources extends ResourceBundle {
    private final Properties properties = new Properties();

    public ViewerResources() {
        try (Reader in = new BufferedReader(
                new InputStreamReader(
                        this.getClass().getResourceAsStream(
                                this.getClass().getSimpleName().replaceAll("Resources", "") + ".properties"
                        ),
                        StandardCharsets.UTF_8))) {
            properties.load(in);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        return properties.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getKeys() {
        return (Enumeration) properties.keys();
    }
}
