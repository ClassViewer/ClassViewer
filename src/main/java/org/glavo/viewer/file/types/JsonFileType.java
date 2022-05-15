package org.glavo.viewer.file.types;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.highlighter.LookAheadAntlrLexerHighlighter;
import org.glavo.viewer.file.types.grammar.Json5Lexer;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;
import java.util.function.IntFunction;

public class JsonFileType extends TextFileType {

    public static final JsonFileType TYPE = new JsonFileType();

    private JsonFileType() {
        super("json");
        this.highlighter = new LookAheadAntlrLexerHighlighter(Json5Lexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token, int tokenIndex, IntFunction<Token> lookahead) {
                switch (token.getType()) {
                    case Json5Lexer.DELIMITER:
                        return Stylesheet.CODE_DELIMITER_CLASSES;
                    case Json5Lexer.BRACKETS:
                        return Stylesheet.CODE_BRACKETS_CLASSES;
                    case Json5Lexer.NUMBER:
                        return Stylesheet.CODE_NUMBER_CLASSES;
                    case Json5Lexer.LITERAL:
                        return Stylesheet.CODE_KEYWORD_CLASSES;
                    case Json5Lexer.STRING:
                    case Json5Lexer.IDENTIFIER:
                        Token t = lookahead.apply(tokenIndex + 1);
                        if (t != null && t.getType() == Json5Lexer.DELIMITER) {
                            return Stylesheet.CODE_PROPERTY_KEY;
                        } else {
                            return Stylesheet.CODE_STRING_CLASSES;
                        }
                    case Json5Lexer.SINGLE_LINE_COMMENT:
                    case Json5Lexer.MULTI_LINE_COMMENT:
                        return Stylesheet.CODE_COMMENT_CLASSES;

                    default:
                        return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("json");
    }
}
