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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class LookAheadAntlrLexerHighlighter implements Highlighter {
    private final Function<CharStream, ? extends Lexer> lexerFactory;

    protected LookAheadAntlrLexerHighlighter(Function<CharStream, ? extends Lexer> lexerFactory) {
        this.lexerFactory = lexerFactory;
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Lexer lexer = lexerFactory.apply(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        List<Token> tokens = new ArrayList<>();
        Token t;
        while ((t = lexer.nextToken()).getType() != Token.EOF) {
            if (t.getChannel() == Token.DEFAULT_CHANNEL) {
                tokens.add(t);
            }
        }

        IntFunction<Token> lookahead = idx -> idx < 0 || idx >= tokens.size() ? null : tokens.get(idx);

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int begin = token.getStartIndex();
            int end = token.getStopIndex() + 1;

            spansBuilder.add(Collections.emptyList(), begin - lastKwEnd);
            spansBuilder.add(getStyleClass(token, i, lookahead), end - begin);
            lastKwEnd = end;
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    protected abstract Collection<String> getStyleClass(Token token, int tokenIndex, IntFunction<Token> lookahead);
}
