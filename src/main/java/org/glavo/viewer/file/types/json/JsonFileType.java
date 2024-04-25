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
package org.glavo.viewer.file.types.json;

import org.antlr.v4.runtime.Token;
import org.glavo.viewer.file.types.TextFileType;
import org.glavo.viewer.highlighter.LookNextAntlrLexerHighlighter;
import org.glavo.viewer.util.Stylesheet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class JsonFileType extends TextFileType {

    public static final JsonFileType TYPE = new JsonFileType();

    private JsonFileType() {
        super("json", Set.of("json", "json5"));
        this.highlighter = new LookNextAntlrLexerHighlighter(Json5Lexer::new) {
            @Override
            protected Collection<String> getStyleClass(Token token, Token nextToken) {
                return switch (token.getType()) {
                    case Json5Lexer.DELIMITER -> Stylesheet.CODE_DELIMITER_CLASSES;
                    case Json5Lexer.BRACKETS -> Stylesheet.CODE_BRACKETS_CLASSES;
                    case Json5Lexer.NUMBER -> Stylesheet.CODE_NUMBER_CLASSES;
                    case Json5Lexer.LITERAL -> Stylesheet.CODE_KEYWORD_CLASSES;
                    case Json5Lexer.STRING, Json5Lexer.IDENTIFIER ->
                            nextToken.getType() == Json5Lexer.DELIMITER ? Stylesheet.CODE_PROPERTY_KEY : Stylesheet.CODE_STRING_CLASSES;
                    case Json5Lexer.SINGLE_LINE_COMMENT, Json5Lexer.MULTI_LINE_COMMENT ->
                            Stylesheet.CODE_COMMENT_CLASSES;
                    default -> List.of();
                };
            }
        };
    }
}
