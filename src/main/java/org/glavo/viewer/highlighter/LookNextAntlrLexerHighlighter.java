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
package org.glavo.viewer.highlighter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public abstract class LookNextAntlrLexerHighlighter implements Highlighter {
    private final Function<CharStream, ? extends Lexer> lexerFactory;

    protected LookNextAntlrLexerHighlighter(Function<CharStream, ? extends Lexer> lexerFactory) {
        this.lexerFactory = lexerFactory;
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Lexer lexer = lexerFactory.apply(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;

        Token current = nextToken(lexer);
        Token next = nextToken(lexer);

        while (current.getType() != Token.EOF) {
            int begin = current.getStartIndex();
            int end = current.getStopIndex() + 1;

            spansBuilder.add(Collections.emptyList(), begin - lastKwEnd);
            spansBuilder.add(getStyleClass(current, next), end - begin);
            lastKwEnd = end;

            current = next;
            next = nextToken(lexer);
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static Token nextToken(Lexer lexer) {
        Token token;
        do {
            token = lexer.nextToken();
        } while (token.getChannel() != Token.DEFAULT_CHANNEL);

        return token;
    }

    protected abstract Collection<String> getStyleClass(Token token, Token nextToken);
}
