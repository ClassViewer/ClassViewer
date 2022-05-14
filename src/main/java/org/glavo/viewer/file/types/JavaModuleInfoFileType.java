package org.glavo.viewer.file.types;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.FilePath;
import org.glavo.viewer.file.types.lexer.JavaLexer;
import org.glavo.viewer.file.types.lexer.JavaModuleInfoLexer;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;

public class JavaModuleInfoFileType extends TextFileType {

    public static final JavaModuleInfoFileType TYPE = new JavaModuleInfoFileType();

    private JavaModuleInfoFileType() {
        super("java-module-info", JavaSourceFileType.TYPE.getImage());
        this.lexerFactory = JavaModuleInfoLexer::new;
    }

    @Override
    public boolean check(FilePath path) {
        return path.getFileName().equals("module-info.java");
    }

    @Override
    protected Collection<String> getStyleClass(Token token) {
        switch (token.getType()) {
            case JavaModuleInfoLexer.KEYWORD:
            case JavaModuleInfoLexer.ANNOTATION:
                return Stylesheet.CODE_KEYWORD_CLASSES;
            case JavaModuleInfoLexer.COMMENT:
                return Stylesheet.CODE_COMMENT_CLASSES;
            case JavaModuleInfoLexer.TERMINATOR:
                return Stylesheet.CODE_TERMINATOR_CLASSES;
            case JavaModuleInfoLexer.BRACKETS:
                return Stylesheet.CODE_BRACKETS_CLASSES;
            default:
                return Collections.emptyList();
        }
    }
}
