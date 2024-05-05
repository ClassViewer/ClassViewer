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
package org.glavo.viewer.file.types.markdown;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.*;
import kala.value.primitive.IntVar;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.resources.Resources;
import org.glavo.viewer.ui.FileTab;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public final class MarkdownFileType extends TextFileType {
    public static final MarkdownFileType TYPE = new MarkdownFileType();

    private static final String stylesheet = Resources.getResource("stylesheet/markdown.css").toExternalForm();

    private final Parser parser = Parser.builder().build();

    private MarkdownFileType() {
        super("markdown", Set.of("md", "MD", "markdown"));
        this.highlighter = text -> {
            Document ast = parser.parse(text);
            HighlighterHelper helper = new HighlighterHelper();
            StyleSpansBuilder<Collection<String>> spansBuilder = helper.spansBuilder;

            helper.addHandler(Heading.class, node -> helper.addStyle("h" + node.getLevel(), node.getTextLength()));

            helper.addHandler(Link.class, link -> handleInlineLinkNode(helper, link));
            helper.addHandler(Image.class, image -> handleInlineLinkNode(helper, image));

            helper.addHandler(Code.class, Stylesheet.CODE_STRING_CLASSES);
            helper.addHandler(CodeBlock.class, Stylesheet.CODE_STRING_CLASSES);
            helper.addHandler(IndentedCodeBlock.class, Stylesheet.CODE_STRING_CLASSES);
            helper.addHandler(FencedCodeBlock.class, Stylesheet.CODE_STRING_CLASSES);

            helper.addHandler(Emphasis.class, node -> {
                helper.addStyle("delimited-marker", node.getOpeningMarker().length());
                helper.addStyle("italic", node.getText().length());
                helper.addStyle("delimited-marker", node.getClosingMarker().length());
            });
            helper.addHandler(StrongEmphasis.class, node -> {
                helper.addStyle("delimited-marker", node.getOpeningMarker().length());
                helper.addStyle("bold", node.getText().length());
                helper.addStyle("delimited-marker", node.getClosingMarker().length());
            });

            helper.visit(ast, text);

            return helper.build();
        };
    }

    private static void handleInlineLinkNode(HighlighterHelper helper, InlineLinkNode link) {
        helper.addStyle("link-text",
                link.getTextOpeningMarker().length()
                        + link.getText().length()
                        + link.getTextClosingMarker().length());

        helper.addEmptyStyle(link.getLinkOpeningMarker().length() + link.getUrlOpeningMarker().length());

        helper.addStyle("link-url", link.getUrl().length());
        helper.addEmptyStyle(link.getUrlClosingMarker().length());

        if (link instanceof Image) {
            helper.addEmptyStyle(((Image) link).getUrlContent().length());
            helper.addStyle(Stylesheet.CODE_STRING_CLASSES,
                    link.getTitleOpeningMarker().length()
                            + link.getTitleOpeningMarker().length()
                            + link.getTitle().length()
                            + link.getTitleClosingMarker().length());
        } else {
            helper.addStyle(Stylesheet.CODE_STRING_CLASSES,
                    link.getTitleOpeningMarker().length()
                            + link.getTitle().length()
                            + link.getTitleClosingMarker().length());
        }

        helper.addEmptyStyle(link.getLinkClosingMarker().length());
    }

    private static final class HighlighterHelper {
        private final IntVar lastEnd = new IntVar();
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        private final NodeVisitor visitor = new NodeVisitor();

        void addStyle(String styleClass, int length) {
            spansBuilder.add(Collections.singletonList(styleClass), length);
        }

        void addStyle(Collection<String> styleClasses, int length) {
            spansBuilder.add(styleClasses, length);
        }

        void addEmptyStyle(int length) {
            spansBuilder.add(Collections.emptyList(), length);
        }

        <N extends Node> void addHandler(Class<N> type, Collection<String> styleClasses) {
            visitor.addHandler(new VisitHandler<>(type, node -> {
                int begin = node.getStartOffset();
                int end = node.getEndOffset();

                spansBuilder.add(Collections.emptyList(), begin - lastEnd.get());
                spansBuilder.add(styleClasses, end - begin);
                lastEnd.set(end);
            }));
        }

        <N extends Node> void addHandler(Class<N> type, Consumer<? super N> consumer) {
            visitor.addHandler(new VisitHandler<>(type, node -> {
                int begin = node.getStartOffset();
                int end = node.getEndOffset();

                spansBuilder.add(Collections.emptyList(), begin - lastEnd.get());
                consumer.accept(node);
                lastEnd.set(end);
            }));
        }

        void visit(Node node, String text) {
            visitor.visitChildren(node);
            spansBuilder.add(Collections.emptyList(), text.length() - lastEnd.get());
        }

        StyleSpans<Collection<String>> build() {
            return spansBuilder.create();
        }
    }

    @Override
    protected void applyHighlighter(FileTab tab, CodeArea area) {
        area.getStylesheets().add(stylesheet);
        super.applyHighlighter(tab, area);
    }
}
