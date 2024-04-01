package org.glavo.viewer.highlighter;

import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.Collections;

@FunctionalInterface
public interface Highlighter {
    static Highlighter defaultHighlighter() {
        return text -> StyleSpans.singleton(Collections.emptyList(), text.length());
    }

    StyleSpans<Collection<String>> computeHighlighting(String text);
}
