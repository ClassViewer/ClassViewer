lexer grammar Json5Lexer;

@header {
package org.glavo.viewer.file.types.json;
}

DELIMITER
    : ':'
    ;

BRACKETS
    : '('
    | ')'
    | '{'
    | '}'
    | '['
    | ']'
    ;

NUMBER
   : SYMBOL? NUMERIC_LITERAL
   ;

// Lexer

SINGLE_LINE_COMMENT
   : '//' .*? (NEWLINE | EOF)
   ;

MULTI_LINE_COMMENT
   : '/*' .*? '*/'
   ;

LITERAL
   : 'true'
   | 'false'
   | 'null'
   | 'Infinity'
   | 'NaN'
   ;

STRING
   : '"' DOUBLE_QUOTE_CHAR* '"'
   | '\'' SINGLE_QUOTE_CHAR* '\''
   ;

fragment DOUBLE_QUOTE_CHAR
   : ~["\\\r\n]
   | ESCAPE_SEQUENCE
   ;
fragment SINGLE_QUOTE_CHAR
   : ~['\\\r\n]
   | ESCAPE_SEQUENCE
   ;
fragment ESCAPE_SEQUENCE
   : '\\'
   ( NEWLINE
   | UNICODE_SEQUENCE       // \u1234
   | ['"\\/bfnrtv]          // single escape char
   | ~['"\\bfnrtv0-9xu\r\n] // non escape char
   | '0'                    // \0
   | 'x' HEX HEX            // \x3a
   )
   ;
fragment NUMERIC_LITERAL
   : INT ('.' [0-9]*)? EXP? // +1.e2, 1234, 1234.5
   | '.' [0-9]+ EXP?        // -.2e3
   | '0' [xX] HEX+          // 0x12345678
   ;

fragment SYMBOL
   : '+' | '-'
   ;
fragment HEX
   : [0-9a-fA-F]
   ;
fragment INT
   : '0' | [1-9] [0-9]*
   ;
fragment EXP
   : [Ee] SYMBOL? [0-9]*
   ;
IDENTIFIER
   : IDENTIFIER_START IDENTIFIER_PART*
   ;
fragment IDENTIFIER_START
   : [\p{L}]
   | '$'
   | '_'
   | '\\' UNICODE_SEQUENCE
   ;
fragment IDENTIFIER_PART
   : IDENTIFIER_START
   | [\p{M}]
   | [\p{N}]
   | [\p{Pc}]
   | '\u200C'
   | '\u200D'
   ;
fragment UNICODE_SEQUENCE
   : 'u' HEX HEX HEX HEX
   ;
fragment NEWLINE
   : '\r\n'
   | [\r\n\u2028\u2029]
   ;
WS
   : [ \t\n\r\u00A0\uFEFF\u2003] + -> channel(HIDDEN)
   ;