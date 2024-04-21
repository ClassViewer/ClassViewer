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
package org.glavo.viewer.file.types.java;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.VirtualFile;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.highlighter.LookNextAntlrLexerHighlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class JavaSourceFileType extends TextFileType {

    public static final JavaSourceFileType TYPE = new JavaSourceFileType();

    private JavaSourceFileType() {
        super("java", Set.of("java"));
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
    public boolean check(VirtualFile file, String ext) {
        return ext.equals("java") && !file.getFileName().equals("module-info.java");
    }
}
