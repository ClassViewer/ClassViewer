module org.glavo.viewer {
    requires static java.desktop;
    requires java.logging;

    requires javafx.controls;

    requires static org.jetbrains.annotations;

    requires org.glavo.jimage;

    // Kala
    requires kala.platform;
    requires kala.template;
    requires kala.compress.archivers.zip;
    requires kala.common;

    requires com.fasterxml.jackson.databind;
    requires com.github.albfernandez.juniversalchardet;
    requires org.apache.commons.imaging;
    requires org.antlr.antlr4.runtime;

    // RichTextFX
    requires org.fxmisc.richtext;
    requires reactfx;

    requires flexmark;
    requires flexmark.util.ast;
    requires flexmark.util.sequence;
    requires org.fxmisc.flowless;
}