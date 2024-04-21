/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.viewer.file.types.java;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.highlighter.Highlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PropertiesFileType extends TextFileType {

    public static final PropertiesFileType TYPE = new PropertiesFileType();

    private PropertiesFileType() {
        super("properties", Set.of("properties"));
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
}
