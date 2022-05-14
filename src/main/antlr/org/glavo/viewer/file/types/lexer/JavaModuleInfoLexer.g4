lexer grammar JavaModuleInfoLexer;

@header {
package org.glavo.viewer.file.types.lexer;
}

KEYWORD
    : 'module'
    | 'open'
    | 'requires'
    | 'exports'
    | 'opens'
    | 'to'
    | 'uses'
    | 'provides'
    | 'with'
    | 'transitive'
    ;

BRACKETS
    : '('
    | ')'
    | '{'
    | '}'
    | '['
    | ']'
    ;

TERMINATOR: ';';

COMMENT
    : '/*' .*? '*/'
    | '//' ~[\r\n]*;

ANNOTATION: '@' ID;

IDENTIFIER: ID;

DELIMITER: ',' | '.';

IGNORED: [ \t\r\n\u000C]+ -> channel(HIDDEN);


fragment ID:         Letter LetterOrDigit*;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;