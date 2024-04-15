package org.glavo.viewer.file.types.aya;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.highlighter.AntlrLexerHighlighter;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.List;

public class AyaSourceFileType extends TextFileType {

    public static final AyaSourceFileType TYPE = new AyaSourceFileType();

    private AyaSourceFileType() {
        super("aya");
        this.forceUTF8 = true;
        this.highlighter = new AntlrLexerHighlighter(AyaLexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token) {
                return switch (token.getType()) {
                    case AyaLexer.KEYWORD -> Stylesheet.CODE_KEYWORD_CLASSES;
                    case AyaLexer.LBRACE, AyaLexer.RBRACE, AyaLexer.LPAREN, AyaLexer.RPAREN, AyaLexer.LGOAL,
                         AyaLexer.RGOAL -> Stylesheet.CODE_BRACKETS_CLASSES;
                    case AyaLexer.COLON, AyaLexer.COLON2, AyaLexer.COMMA, AyaLexer.DOT -> Stylesheet.CODE_DELIMITER_CLASSES;
                    case AyaLexer.SUCHTHAT, AyaLexer.IMPLIES, AyaLexer.TO -> Stylesheet.CODE_OPERATOR_CLASSES;
                    case AyaLexer.NUMBER -> Stylesheet.CODE_NUMBER_CLASSES;
                    case AyaLexer.STRING, AyaLexer.INCOMPLETE_STRING -> Stylesheet.CODE_STRING_CLASSES;
                    case AyaLexer.COMMENT, AyaLexer.LINE_COMMENT, AyaLexer.DOC_COMMENT -> Stylesheet.CODE_COMMENT_CLASSES;
                    default -> List.of();
                };
            }
        };
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("aya");
    }
}
