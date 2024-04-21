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
package org.glavo.viewer.file.types.aya;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.highlighter.AntlrLexerHighlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AyaSourceFileType extends TextFileType {

    public static final AyaSourceFileType TYPE = new AyaSourceFileType();

    private AyaSourceFileType() {
        super("aya", Set.of("aya"));
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
}
