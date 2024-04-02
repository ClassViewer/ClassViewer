package org.glavo.viewer.file.types.java;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.highlighter.LookNextAntlrLexerHighlighter;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.List;

public class JavaSourceFileType extends TextFileType {

    public static final JavaSourceFileType TYPE = new JavaSourceFileType();

    private JavaSourceFileType() {
        super("java");
        this.highlighter = new LookNextAntlrLexerHighlighter(JavaLexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token, Token nextToken) {
                return switch (token.getType()) {
                    case JavaLexer.VAR ->
                            nextToken == null || !(nextToken.getType() == JavaLexer.IDENTIFIER || nextToken.getType() == JavaLexer.VAR)
                                    ? List.of()
                                    : Stylesheet.CODE_KEYWORD_CLASSES;
                    case JavaLexer.KEY_WORD, JavaLexer.ANNOTATION -> Stylesheet.CODE_KEYWORD_CLASSES;
                    case JavaLexer.STRING -> Stylesheet.CODE_STRING_CLASSES;
                    case JavaLexer.COMMENT -> Stylesheet.CODE_COMMENT_CLASSES;
                    case JavaLexer.OPERATOR -> Stylesheet.CODE_OPERATOR_CLASSES;
                    case JavaLexer.TERMINATOR -> Stylesheet.CODE_TERMINATOR_CLASSES;
                    case JavaLexer.DELIMITER -> Stylesheet.CODE_DELIMITER_CLASSES;
                    case JavaLexer.NUMBER -> Stylesheet.CODE_NUMBER_CLASSES;
                    case JavaLexer.BRACKETS -> Stylesheet.CODE_BRACKETS_CLASSES;
                    default -> List.of();
                };
            }
        };
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("java") && !path.getFileName().equals("module-info.java");
    }
}
