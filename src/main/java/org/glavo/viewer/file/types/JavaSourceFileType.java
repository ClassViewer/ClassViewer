package org.glavo.viewer.file.types;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.lexer.JavaLexer;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;

public class JavaSourceFileType extends TextFileType {

    public static final JavaSourceFileType TYPE = new JavaSourceFileType();

    private JavaSourceFileType() {
        super("java");
        this.lexerFactory = JavaLexer::new;
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileNameExtension().equals("java") && !path.getFileName().equals("module-info.java");
    }

    @Override
    protected Collection<String> getStyleClass(Token token) {
        switch (token.getType()) {
            case JavaLexer.KEY_WORD:
            case JavaLexer.ANNOTATION:
                return Stylesheet.CODE_KEYWORD_CLASSES;
            case JavaLexer.STRING:
                return Stylesheet.CODE_STRING_CLASSES;
            case JavaLexer.COMMENT:
                return Stylesheet.CODE_COMMENT_CLASSES;
            case JavaLexer.OPERATOR:
                return Stylesheet.CODE_OPERATOR_CLASSES;
            case JavaLexer.TERMINATOR:
                return Stylesheet.CODE_TERMINATOR_CLASSES;
            case JavaLexer.NUMBER:
                return Stylesheet.CODE_NUMBER_CLASSES;
            case JavaLexer.BRACKETS:
                return Stylesheet.CODE_BRACKETS_CLASSES;
            default:
                return Collections.emptyList();
        }
    }
}
