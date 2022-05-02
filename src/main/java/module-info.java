module org.glavo.viewer {
    requires static java.desktop;
    requires java.logging;

    requires javafx.controls;

    requires org.glavo.jimage;
    requires kala.platform;
    requires kala.template;
    requires kala.compress.archivers.zip;

    requires com.fasterxml.jackson.databind;
}