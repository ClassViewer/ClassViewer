package org.glavo.viewer.file.highlighter;

import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;
import java.util.Collections;

public class Highlighter {
    public Highlighter() {
    }

    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        return StyleSpans.singleton(Collections.emptyList(), text.length());
    }
}
