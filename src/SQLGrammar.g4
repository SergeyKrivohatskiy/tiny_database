grammar SQLGrammar;

options {
language = Java;
}

@header {
package grammar;

import java.util.*;
import queries.*;
}

@members {

}

script returns [List<IQuery> result]
@init {
    $result = new ArrayList<>();
}
    :   (   query {
        $result.add($query.result);
    }
    )+ EOF
    ;

query returns [IQuery result]
@init {
    $result = null;
}
    :   ( (   createTable {
        $result = $createTable.result;
    }
    ) | (   selectFrom {
        $result = $selectFrom.result;
    }
    ) ) SEMICOLON
    ;

createTable returns [CreateTableQuery result]
@init {
    List<Attribute> attributes = new ArrayList<>();
}
    :   CREATE TABLE firstLevelId LEFT_PARENTHESIS attribute {
        $result = new CreateTableQuery($firstLevelId.text, attributes);
        attributes.add($attribute.result);
    }
    (
        COMMA (
            attribute {
                attributes.add($attribute.result);
            }
        )
    )*
    RIGHT_PARENTHESIS
    ;

attribute returns [Attribute result]
    :   firstLevelId dataType {
        $result = new Attribute($firstLevelId.text, $dataType.result);
    }
    ;

dataType returns [Attribute.DataType result]
@init {
    $result = null;
}
    :   INTEGER_TYPE {
        $result = new Attribute.IntegerType();
    }
    |   DOUBLE_TYPE {
        $result = new Attribute.DoubleType();
    }
    |   varCharType {
        $result = new Attribute.VarcharType($varCharType.length);
    }
    ;

varCharType returns [int length]
    :   VAR_CHAR_TYPE LEFT_PARENTHESIS integerLiteral RIGHT_PARENTHESIS {
        $length = $integerLiteral.value;
    }
    ;

integerLiteral returns [int value]
    :   SIGN? DECIMAL_DIGIT+ {
        $value = Integer.valueOf($text);
    }
    ;

doubleLiteral returns [double value]
    :   SIGN? DECIMAL_DIGIT+ DECIMAL_POINT DECIMAL_DIGIT*
    |   SIGN? DECIMAL_POINT? DECIMAL_DIGIT+ {
        $value = Double.valueOf($text);
    }
    ;

firstLevelId
    :   ( UNDERLINE idSuffix
    ) | ( (
            LOWER_CASE
        |   UPPER_CASE
        ) idSuffix?
    )
    ;

idSuffix
    :   (
        LOWER_CASE
    |   UPPER_CASE
    |   DECIMAL_DIGIT
    |   UNDERLINE
    )+
    ;

selectFrom returns [SelectFromQuery result]
    :   SELECT filter FROM table
    ;

filter
    :   ASTERISC
    |   firstLevelId (COMMA firstLevelId)*
    ;

table
    :   firstLevelId (join)*
    ;

join
    :   INNER JOIN firstLevelId ON joinStatement
    ;

joinStatement
    :   secondLevelId EQUALS secondLevelId
    ;

secondLevelId
    :   firstLevelId ~WHITE_SPACE DOT ~WHITE_SPACE firstLevelId
    ;

INTEGER_TYPE
    :   'INTEGER'
    ;

DOUBLE_TYPE
    :   'DOUBLE'
    ;

VAR_CHAR_TYPE
    :   'VARCHAR'
    ;

CREATE
    :   'CREATE'
    ;

TABLE
    :   'TABLE'
    ;

SELECT
    :   'SELECT'
    ;

FROM
    :   'FROM'
    ;

INNER
    :   'INNER'
    ;

JOIN
    :   'JOIN'
    ;

ON
    :   'ON'
    ;

COMMA
    :   ','
    ;

ASTERISC
    :   '*'
    ;

LEFT_PARENTHESIS
    :   '('
    ;

RIGHT_PARENTHESIS
    :   ')'
    ;

SEMICOLON
    :   ';'
    ;

WHITE_SPACE
    :   ( ' '
        | '\t'
        | '\n'
        ) { skip(); }
    ;

LOWER_CASE
    :   'a' .. 'z'
    ;

UPPER_CASE
    :   'A' .. 'Z'
    ;

UNDERLINE
    :   '_'
    ;

DECIMAL_DIGIT
    :   '0' .. '9'
    ;

SIGN
    :   '+'
    |   '-'
    ;

DECIMAL_POINT
    :   '.'
    ;

DOT
    :   '.'
    ;

EQUALS
    :   '='
    ;