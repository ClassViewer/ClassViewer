package org.glavo.viewer.file.types;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.highlighter.AntlrLexerHighlighter;
import org.glavo.viewer.file.types.grammar.AyaLexer;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;

public class AyaSourceFileType extends TextFileType {

    public static final AyaSourceFileType TYPE = new AyaSourceFileType();

    private AyaSourceFileType() {
        super("aya");
        this.forceUTF8 = true;
        this.highlighter = new AntlrLexerHighlighter(AyaLexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token) {
                switch (token.getType()) {
                    case AyaLexer.KEYWORD:
                        return Stylesheet.CODE_KEYWORD_CLASSES;
                    case AyaLexer.LBRACE:
                    case AyaLexer.RBRACE:
                    case AyaLexer.LPAREN:
                    case AyaLexer.RPAREN:
                    case AyaLexer.LGOAL:
                    case AyaLexer.RGOAL:
                        return Stylesheet.CODE_BRACKETS_CLASSES;
                    case AyaLexer.COLON:
                    case AyaLexer.COLON2:
                    case AyaLexer.COMMA:
                    case AyaLexer.DOT:
                        return Stylesheet.CODE_DELIMITER_CLASSES;
                    case AyaLexer.SUCHTHAT:
                    case AyaLexer.IMPLIES:
                    case AyaLexer.TO:
                        return Stylesheet.CODE_OPERATOR_CLASSES;
                    case AyaLexer.NUMBER:
                        return Stylesheet.CODE_NUMBER_CLASSES;
                    case AyaLexer.STRING:
                    case AyaLexer.INCOMPLETE_STRING:
                        return Stylesheet.CODE_STRING_CLASSES;
                    case AyaLexer.COMMENT:
                    case AyaLexer.LINE_COMMENT:
                    case AyaLexer.DOC_COMMENT:
                        return Stylesheet.CODE_COMMENT_CLASSES;
                    default:
                        return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("aya");
    }
}
