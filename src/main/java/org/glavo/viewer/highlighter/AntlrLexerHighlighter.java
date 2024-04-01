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

public abstract class AntlrLexerHighlighter implements Highlighter {
    private final Function<CharStream, ? extends Lexer> lexerFactory;

    protected AntlrLexerHighlighter(Function<CharStream, ? extends Lexer> lexerFactory) {
        this.lexerFactory = lexerFactory;
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Lexer lexer = lexerFactory.apply(CharStreams.fromString(text));
        lexer.removeErrorListeners();

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;

        Token token;
        while ((token = lexer.nextToken()).getType() != Token.EOF) {
            int begin = token.getStartIndex();
            int end = token.getStopIndex() + 1;

            spansBuilder.add(Collections.emptyList(), begin - lastKwEnd);
            spansBuilder.add(getStyleClass(token), end - begin);
            lastKwEnd = end;
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    protected abstract Collection<String> getStyleClass(Token token);
}
