/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr, Sam Harwell
 Copyright (c) 2017 Ivan Kochurkin (upgrade to Java 8)
 Copyright (c) 2021 Michał Lorek (upgrade to Java 11)
 Copyright (c) 2022 Michał Lorek (upgrade to Java 17)
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

lexer grammar JavaLexer;

@header {
package org.glavo.viewer.file.types.java;
}

KEY_WORD
    : 'abstract'
    | 'assert'
    | 'boolean'
    | 'break'
    | 'byte'
    | 'case'
    | 'catch'
    | 'char'
    | 'class'
    | 'const'
    | 'continue'
    | 'default'
    | 'do'
    | 'double'
    | 'else'
    | 'enum'
    | 'extends'
    | 'false'
    | 'final'
    | 'finally'
    | 'float'
    | 'for'
    | 'if'
    | 'goto'
    | 'implements'
    | 'import'
    | 'instanceof'
    | 'int'
    | 'interface'
    | 'long'
    | 'native'
    | 'new'
    | 'non-sealed'
    | 'null'
    | 'package'
    | 'private'
    | 'protected'
    | 'public'
    | 'record'
    | 'return'
    | 'sealed'
    | 'short'
    | 'static'
    | 'strictfp'
    | 'super'
    | 'switch'
    | 'synchronized'
    | 'permits'
    | 'this'
    | 'throw'
    | 'throws'
    | 'transient'
    | 'true'
    | 'try'
    | 'void'
    | 'volatile'
    | 'while';

VAR: 'var';

NUMBER
    : ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?
    | '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?
    | '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?
    | '0' [bB] [01] ([01_]* [01])? [lL]?
    | FLOAT_LITERAL
    | HEX_FLOAT_LITERAL
    ;

fragment FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?
             |       Digits (ExponentPart [fFdD]? | [fFdD])
             ;

fragment HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;

STRING
    : '\'' (~['\\\r\n] | EscapeSequence) '\''
    | '"' (~["\\\r\n] | EscapeSequence)* '"'
    | '"""' [ \t]* [\r\n] (. | EscapeSequence)*? '"""'
    ;

BRACKETS
    : '('
    | ')'
    | '{'
    | '}'
    | '['
    | ']'
    ;

OPERATOR
    : '='
    | '>'
    | '<'
    | '!'
    | '~'
    | '?'
    | ':'
    | '=='
    | '<='
    | '>='
    | '!='
    | '&&'
    | '||'
    | '++'
    | '--'
    | '+'
    | '-'
    | '*'
    | '/'
    | '&'
    | '|'
    | '^'
    | '%'
    | '+='
    | '-='
    | '*='
    | '/='
    | '&='
    | '|='
    | '^='
    | '%='
    | '<<='
    | '>>='
    | '>>>='
    | '->'
    | '@'
    | '...';

TERMINATOR: ';';

COMMENT
    : '/*' .*? '*/'
    | '//' ~[\r\n]*;

ANNOTATION: '@' ID;

IDENTIFIER: ID;

DELIMITER: ',' | '.' | '::';

IGNORED: [ \t\r\n\u000C]+ -> channel(HIDDEN);

fragment ExponentPart
    : [eE] [+-]? Digits
    ;

fragment EscapeSequence
    : '\\' [btnfr"'\\]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;

fragment ID:         Letter LetterOrDigit*;

fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;

fragment LetterOrDigit
    : Letter
    | [0-9]
    ;

fragment Letter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    ;