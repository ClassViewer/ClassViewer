package org.glavo.viewer.file.types;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.highlighter.Highlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesFileType extends TextFileType {

    public static final PropertiesFileType TYPE = new PropertiesFileType();

    private PropertiesFileType() {
        super("properties");
        this.highlighter = new Highlighter() {
            private final Pattern regex = Pattern.compile(
                    "(?<comment>[#!]\\S*)|(?<property>(?<key>([^=:\\r\\n]|\\\\=|\\\\:)+)[=:](?<value>([^\\r\\n]|\\\\\\r|\\\\\\n)+))"
            );

            @Override
            public StyleSpans<Collection<String>> computeHighlighting(String text) {
                Matcher matcher = regex.matcher(text);
                int lastKwEnd = 0;
                StyleSpansBuilder<Collection<String>> spansBuilder
                        = new StyleSpansBuilder<>();
                while (matcher.find()) {
                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);

                    if (matcher.group("comment") != null) {
                        spansBuilder.add(Stylesheet.CODE_COMMENT_CLASSES, matcher.end() - matcher.start());
                    } else {
                        spansBuilder.add(Stylesheet.CODE_PROPERTY_KEY, matcher.group("key").length());
                        spansBuilder.add(Stylesheet.CODE_DELIMITER_CLASSES, 1);
                        spansBuilder.add(Stylesheet.CODE_STRING_CLASSES, matcher.group("value").length());
                    }
                    lastKwEnd = matcher.end();

                }
                spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
                return spansBuilder.create();
            }
        };
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("properties");
    }
}
