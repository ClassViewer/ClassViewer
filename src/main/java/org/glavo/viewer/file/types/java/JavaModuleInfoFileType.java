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
import org.glavo.viewer.highlighter.AntlrLexerHighlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class JavaModuleInfoFileType extends TextFileType {

    public static final JavaModuleInfoFileType TYPE = new JavaModuleInfoFileType();

    private JavaModuleInfoFileType() {
        super("java-module-info", JavaSourceFileType.TYPE.getImage(), Set.of());
        this.highlighter = new AntlrLexerHighlighter(JavaModuleInfoLexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token) {
                return switch (token.getType()) {
                    case JavaModuleInfoLexer.KEYWORD, JavaModuleInfoLexer.ANNOTATION -> Stylesheet.CODE_KEYWORD_CLASSES;
                    case JavaModuleInfoLexer.COMMENT -> Stylesheet.CODE_COMMENT_CLASSES;
                    case JavaModuleInfoLexer.TERMINATOR -> Stylesheet.CODE_TERMINATOR_CLASSES;
                    case JavaModuleInfoLexer.DELIMITER -> Stylesheet.CODE_DELIMITER_CLASSES;
                    case JavaModuleInfoLexer.BRACKETS -> Stylesheet.CODE_BRACKETS_CLASSES;
                    default -> Collections.emptyList();
                };
            }
        };
    }

    @Override
    public boolean check(VirtualFile file, String ext) {
        return file.getFileName().equals("module-info.java");
    }
}
