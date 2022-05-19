lexer grammar AyaLexer;

@header {
package org.glavo.viewer.file.types.aya;
}

KEYWORD
    : 'infix'
    | 'infixl'
    | 'infixr'
    | 'tighter'
    | 'looser'
    | 'example'
    | 'counterexample'
    | 'ulift'
    | '\u2191'
    | 'Type'
    | 'as'
    | 'open'
    | 'import'
    | 'public'
    | 'private'
    | 'using'
    | 'hiding'
    | 'coerce'
    | 'opaque'
    | 'inline'
    | 'overlap'
    | 'module'
    | 'bind'
    | 'match'
    | 'variable'
    | 'def'
    | 'struct'
    | 'data'
    | 'prim'
    | 'extends'
    | 'new'
    | 'pattern'
    | 'do'
    | 'codata'
    | 'let'
    | 'in'
    | 'completed'
    | 'Sig'
    | '\u03A3'
    | '\\'
    | '\u03BB'
    | 'Pi'
    | '\u03A0'
    | 'forall'
    | '\u2200';


TO : '->' | '\u2192';
IMPLIES : '=>' | '\u21D2';
SUCHTHAT : '**';
DOT : '.';
BAR : '|';
COMMA : ',';
COLON : ':';
COLON2 : '::';

// markers
LBRACE : '{';
RBRACE : '}';
LPAREN : '(';
RPAREN : ')';
LGOAL : '{?';
RGOAL : '?}';

// literals
NUMBER : [0-9]+;
CALM_FACE : '_';
STRING : INCOMPLETE_STRING '"';
INCOMPLETE_STRING : '"' (~["\\\r\n] | ESCAPE_SEQ)*;
fragment ESCAPE_SEQ : '\\' [btnfr"'\\] | OCT_ESCAPE | UNICODE_ESCAPE;
fragment OCT_ESCAPE : '\\' OCT_DIGIT OCT_DIGIT? | '\\' [0-3] OCT_DIGIT OCT_DIGIT;
fragment UNICODE_ESCAPE : '\\' 'u'+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT;
fragment HEX_DIGIT : [0-9a-fA-F];
fragment OCT_DIGIT : [0-8];

// identifier
fragment AYA_SIMPLE_LETTER : [~!@#$%^&*+=<>?/|[\u005Da-zA-Z_\u2200-\u22FF];
fragment AYA_UNICODE : [\u0080-\uFEFE] | [\uFF00-\u{10FFFF}]; // exclude U+FEFF which is a truly invisible char
fragment AYA_LETTER : AYA_SIMPLE_LETTER | AYA_UNICODE;
fragment AYA_LETTER_FOLLOW : AYA_LETTER | [0-9'-];
REPL_COMMAND : ':' AYA_LETTER_FOLLOW+;
ID : AYA_LETTER AYA_LETTER_FOLLOW* | '-' AYA_LETTER AYA_LETTER_FOLLOW*;

// whitespaces
WS : [ \t\r\n]+ -> channel(HIDDEN);
fragment COMMENT_CONTENT : ~[\r\n]*;
DOC_COMMENT : '--|' COMMENT_CONTENT;
LINE_COMMENT : '--' COMMENT_CONTENT -> channel(HIDDEN);
COMMENT : '{-' (COMMENT|.)*? '-}' -> channel(HIDDEN);

// avoid token recognition error in REPL
ERROR_CHAR : .;
