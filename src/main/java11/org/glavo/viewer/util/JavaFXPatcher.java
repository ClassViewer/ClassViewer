package org.glavo.viewer.util;

import jdk.internal.loader.BuiltinClassLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

public final class JavaFXPatcher {
    private static final ResourceBundle resources = ResourceBundle.getBundle("viewer.patcher"/* , UTF8Control.Control */);

    public static final String PLATFORM_CLASSIFIER = currentPlatformClassifier();

    private static String currentPlatformClassifier() {
        if (OS.getCurrent() == OS.LINUX) {
            switch (Arch.getCurrent()) {
                case X86_64:
                    return "linux";
                case ARM64:
                    return "linux-aarch64";
            }
        } else if (OS.getCurrent() == OS.MACOS) {
            switch (Arch.getCurrent()) {
                case X86_64:
                    return "mac";
                case ARM64:
                    return "mac-aarch64";
            }
        } else if (OS.getCurrent().isWindows()) {
            switch (Arch.getCurrent()) {
                case X86_64:
                    return "win";
                case X86:
                    return "win-x86";
            }
        }
        return null;
    }

    static final class DependencyDescriptor {
        public static final List<DependencyDescriptor> ALL = loadDependencies();

        public final String module;
        public final String groupId;
        public final String artifactId;
        public final String version;
        public final String classifier;

        private DependencyDescriptor(String module, String groupId, String artifactId, String version, String classifier) {
            this.module = module;
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.classifier = classifier;
        }

        private static List<DependencyDescriptor> loadDependencies() {
            if (PLATFORM_CLASSIFIER == null) {
                return Collections.emptyList();
            }

            Properties properties = new Properties();
            try (final InputStream input = JavaFXPatcher.class.getResourceAsStream("/viewer/openjfx.properties")) {
                //noinspection ConstantConditions
                properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new AssertionError(e);
            }

            final String[] modules = properties.getProperty("modules").split(";");
            final String version = properties.getProperty("version");

            final DependencyDescriptor[] dependencies = new DependencyDescriptor[modules.length];

            for (int i = 0; i < modules.length; i++) {
                String module = modules[i];

                dependencies[i] = new DependencyDescriptor(
                        module,
                        "org.openjfx",
                        module.replace('.', '-'),
                        version,
                        PLATFORM_CLASSIFIER
                );
            }
            //noinspection Java9CollectionFactory
            return Collections.unmodifiableList(Arrays.asList(dependencies));
        }

        public String fileName() {
            return artifactId + "-" + version + "-" + classifier + ".jar";
        }
    }

    static final class Repository {
        public static final List<Repository> REPOSITORIES;

        public static final Repository MAVEN_CENTRAL =
                new Repository(resources.getString("viewer.patcher.repositories.maven_central"), "https://repo1.maven.org/maven2");
        public static final Repository ALIYUN_MIRROR =
                new Repository(resources.getString("viewer.patcher.repositories.aliyun_mirror"), "https://maven.aliyun.com/repository/central");

        public static final Repository DEFAULT;

        static {
            if (System.getProperty("user.country", "").equalsIgnoreCase("CN")) {
                DEFAULT = Repository.ALIYUN_MIRROR;
            } else {
                DEFAULT = Repository.MAVEN_CENTRAL;
            }
            //noinspection Java9CollectionFactory
            REPOSITORIES = Collections.unmodifiableList(Arrays.asList(MAVEN_CENTRAL, ALIYUN_MIRROR));
        }

        private final String name;
        private final String url;

        Repository(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String resolveDependencyURL(DependencyDescriptor descriptor) {
            return String.format("%s/%s/%s/%s/%s",
                    url,
                    descriptor.groupId.replace('.', '/'),
                    descriptor.artifactId, descriptor.version,
                    descriptor.fileName());
        }
    }

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

    private static Repository showChooseRepositoryDialog() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (String line : resources.getString("viewer.patcher.text").split("\n")) {
            panel.add(new JLabel(line));
        }

        final ButtonGroup buttonGroup = new ButtonGroup();

        for (Repository repository : Repository.REPOSITORIES) {
            final JRadioButton button = new JRadioButton(repository.name);
            button.putClientProperty("repository", repository);
            buttonGroup.add(button);
            panel.add(button);
            if (repository == Repository.DEFAULT) {
                button.setSelected(true);
            }
        }

        int res = JOptionPane.showConfirmDialog(null, panel, resources.getString("viewer.patcher.download.title"), JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            final Enumeration<AbstractButton> buttons = buttonGroup.getElements();
            while (buttons.hasMoreElements()) {
                final AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    return (Repository) button.getClientProperty("repository");
                }
            }
        } else {
            System.exit(0);
        }
        throw new AssertionError();
    }

    public static void tryPatch() throws IOException {
        // Unsupported Platform
        if (PLATFORM_CLASSIFIER == null) {
            missJavaFX();
        }

        final Path openjfxDir;
        {
            final String home = System.getProperty("viewer.home");
            openjfxDir = home == null
                    ? Paths.get(System.getProperty("user.home"), ".viewer", "openjfx")
                    : Paths.get(home, "openjfx");
            if (Files.notExists(openjfxDir)) {
                Files.createDirectories(openjfxDir);
            }
        }

        ArrayList<DependencyDescriptor> missingDependencies = new ArrayList<>();
        for (DependencyDescriptor dependency : DependencyDescriptor.ALL) {
            if (Files.notExists(openjfxDir.resolve(dependency.fileName()))) {
                missingDependencies.add(dependency);
            }
        }

        if (!missingDependencies.isEmpty()) {
            final Repository repo = showChooseRepositoryDialog();

            final ProgressFrame frame = new ProgressFrame();
            frame.setVisible(true);
            int progress = 0;

            for (DependencyDescriptor dependency : missingDependencies) {
                int currentProgress = ++progress;
                String url = repo.resolveDependencyURL(dependency);

                SwingUtilities.invokeLater(() -> {
                    frame.setStatus(url);
                    frame.setProgress(currentProgress, missingDependencies.size());
                });

                try (InputStream input = new URL(url).openStream()) {
                    Files.copy(input, openjfxDir.resolve(dependency.fileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            frame.dispose();
        }

        final ArrayList<String> modules = new ArrayList<>();
        final ArrayList<Path> jarPaths = new ArrayList<>();
        for (DependencyDescriptor dependency : DependencyDescriptor.ALL) {
            modules.add(dependency.module);
            jarPaths.add(openjfxDir.resolve(dependency.fileName()));
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
