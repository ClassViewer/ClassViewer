package org.glavo.viewer.util;

import jdk.internal.loader.BuiltinClassLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public final class JavaFXPatcher {
    private static final ResourceBundle resources = ResourceBundle.getBundle("viewer.patcher"/* , UTF8Control.Control */);

    private static void missJavaFX() {
        JOptionPane.showMessageDialog(
                null,
                resources.getString("viewer.javafx.missing.text"),
                resources.getString("viewer.javafx.missing.title"),
                JOptionPane.ERROR_MESSAGE
        );
        System.err.println("Patch JavaFX Failed");
        System.exit(1);
    }

    private static <T> T showChooseSourceDialog(Object[][] sources, int defaultIndex) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(resources.getString("viewer.patcher.text.1")));
        panel.add(new JLabel(resources.getString("viewer.patcher.text.2")));

        final ButtonGroup group = new ButtonGroup();

        final JRadioButton[] buttons = new JRadioButton[sources.length];

        for (int i = 0; i < sources.length; i++) {
            Object[] source = sources[i];

            final String key = (String) source[0];
            final JRadioButton button = new JRadioButton(resources.getString(key));
            buttons[i] = button;
            if (i == defaultIndex) {
                button.setSelected(true);
            }
            group.add(button);
            panel.add(button);
        }

        int opt = JOptionPane.showConfirmDialog(
                null, panel,
                resources.getString("viewer.patcher.title"),
                JOptionPane.YES_NO_OPTION
        );

        if (opt == JOptionPane.YES_OPTION) {
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].isSelected()) {
                    @SuppressWarnings("unchecked") final T res = (T) sources[i][1];
                    return res;
                }
            }
        } else {
            System.exit(0);
        }
        throw new AssertionError();
    }

    public static void tryPatch() throws IOException {
        final Arch arch = Arch.getCurrent();

        //noinspection SwitchStatementWithTooFewBranches
        switch (arch) {
            case X86_64:
                patchX86_64();
                break;
            default:
                missJavaFX();
        }
    }

    private static void patchX86_64() throws IOException {
        final OS os = OS.getCurrent();

        final String classifier;
        if (os == OS.WINDOWS) {
            classifier = "win";
        } else if (os == OS.MACOS) {
            classifier = "mac";
        } else if (os == OS.LINUX) {
            classifier = "linux";
        } else {
            missJavaFX();
            throw new AssertionError();
        }

        final String[][] dependencies = {
                {"javafx.base", "org.openjfx", "javafx-base", "16", null},
                {"javafx.graphics", "org.openjfx", "javafx-graphics", "16", null},
                {"javafx.controls", "org.openjfx", "javafx-controls", "16", null}
        };
        for (String[] dependency : dependencies) {
            dependency[4] = String.format("%s-%s-%s.jar", dependency[2], dependency[3], classifier);
        }

        final Path openjfxDir;
        {
            final String home = System.getProperty("viewer.home");
            openjfxDir = home == null
                    ? Paths.get(System.getProperty("user.home"), ".viewer", "openjfx", "x86_64")
                    : Paths.get(home, "openjfx", "x86_64");

            if (Files.notExists(openjfxDir)) {
                Files.createDirectories(openjfxDir);
            }
        }

        ArrayList<String[]> missingDependencies = new ArrayList<>();
        for (String[] dependency : dependencies) {
            if (Files.notExists(openjfxDir.resolve(dependency[4]))) {
                missingDependencies.add(dependency);
            }
        }

        if (!missingDependencies.isEmpty()) {
            final String[][] sources = {
                    {"viewer.patcher.sources.maven_central", "https://repo1.maven.org/maven2/%s/%s/%s/%s"},
                    {"viewer.patcher.sources.aliyun_mirror", "https://maven.aliyun.com/repository/central/%s/%s/%s/%s"}
            };

            final String sourceTemplate =
                    showChooseSourceDialog(sources, "CN".equalsIgnoreCase(System.getProperty("user.country", "")) ? 1 : 0);

            // fetch dependencies

            final ProgressFrame frame = new ProgressFrame();
            frame.setVisible(true);

            int progress = 0;
            for (String[] dependency : missingDependencies) {
                int currentProgress = ++progress;
                String url = String.format(sourceTemplate, dependency[1].replace('.', '/'), dependency[2], dependency[3], dependency[4]);

                SwingUtilities.invokeLater(() -> {
                    frame.setStatus(url);
                    frame.setProgress(currentProgress, missingDependencies.size());
                });

                try (InputStream is = new URL(url).openStream()) {
                    Files.copy(is, openjfxDir.resolve(dependency[4]), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            frame.dispose();
        }

        final ArrayList<String> modules = new ArrayList<>();
        final ArrayList<Path> jarPaths = new ArrayList<>();
        for (String[] dependency : dependencies) {
            modules.add(dependency[0]);
            jarPaths.add(openjfxDir.resolve(dependency[4]));
        }
        patchRuntime(modules, jarPaths.toArray(new Path[0]));
    }


    private static void patchRuntime(Collection<String> modules, Path[] jarPaths) {

        // Find all modules
        ModuleFinder finder = ModuleFinder.of(jarPaths);

        // Load all modules as unnamed module
        for (ModuleReference mref : finder.findAll()) {
            ((BuiltinClassLoader) ClassLoader.getSystemClassLoader()).loadModule(mref);
        }

        // Define all modules
        Configuration config = Configuration.resolveAndBind(finder, Collections.singletonList(ModuleLayer.boot().configuration()), finder, modules);
        ModuleLayer layer = ModuleLayer.defineModules(config, Collections.singletonList(ModuleLayer.boot()), name -> ClassLoader.getSystemClassLoader()).layer();
    }

    public static class ProgressFrame extends JDialog {

        private final JProgressBar progressBar;
        private final JLabel progressText;

        public ProgressFrame() {
            super((Dialog) null);

            JPanel panel = new JPanel();

            setResizable(false);
            setTitle(resources.getString("viewer.patcher.download.title"));
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setBounds(100, 100, 600, 150);
            setContentPane(panel);
            setLocationRelativeTo(null);

            GridBagLayout gridBagLayout = new GridBagLayout();
            gridBagLayout.columnWidths = new int[]{600, 0};
            gridBagLayout.rowHeights = new int[]{0, 0, 0, 200};
            gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
            gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0};
            panel.setLayout(gridBagLayout);

            progressText = new JLabel("");
            GridBagConstraints gbc_lblProgressText = new GridBagConstraints();
            gbc_lblProgressText.insets = new Insets(10, 0, 5, 0);
            gbc_lblProgressText.gridx = 0;
            gbc_lblProgressText.gridy = 0;
            panel.add(progressText, gbc_lblProgressText);

            progressBar = new JProgressBar();
            GridBagConstraints gbc_progressBar = new GridBagConstraints();
            gbc_progressBar.insets = new Insets(0, 25, 5, 25);
            gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
            gbc_progressBar.gridx = 0;
            gbc_progressBar.gridy = 1;
            panel.add(progressBar, gbc_progressBar);

            JButton btnCancel = new JButton(resources.getString("viewer.patcher.download.cancel.text"));
            btnCancel.addActionListener(e -> System.exit(-1));

            GridBagConstraints gbc_btnCancel = new GridBagConstraints();
            gbc_btnCancel.insets = new Insets(0, 25, 5, 25);
            gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
            gbc_btnCancel.gridx = 0;
            gbc_btnCancel.gridy = 2;
            panel.add(btnCancel, gbc_btnCancel);
        }

        public void setStatus(String status) {
            progressText.setText(status);
        }

        public void setProgress(int current, int total) {
            progressBar.setValue(current);
            progressBar.setMaximum(total);
        }
    }
}
