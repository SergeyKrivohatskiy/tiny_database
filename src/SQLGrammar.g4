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

script
    :   query+ EOF
    ;

query returns [IQuery result]
@init {
    $result = null;
}
    :   (   createTable {
        $result = $createTable.result;
    }
    ) SEMICOLON
    ;

createTable returns [CreateTableQuery result]
@init {
    List<Attribute> attributes = new ArrayList<>();
}
    :   CREATE TABLE id LEFT_PARENTHESIS attribute {
        $result = new CreateTableQuery($id.text, attributes);
        attributes.add($attribute.result);
    }
    (
        COMMA (
            attribute {
                attributes.add($attribute.result);
            }
        //|   tableConstraint
        )
    )*
    RIGHT_PARENTHESIS
    ;

attribute returns [Attribute result]
    :   id dataType //attributeConstraint*
    {
        $result = new Attribute($id.text, $dataType.result);
    }
    ;

//tableConstraint : ; // TODO
//attributeConstraint : ; // TODO

dataType returns [Attribute.DataType result]
@init {
    $result = null;
}
    :   INTEGER_TYPE {
        $result = Attribute.DataType.INTEGER;
    }
    |   charType {
        $result = Attribute.DataType.CHAR;
    }
    |   varCharType {
        $result = Attribute.DataType.VARCHAR;
    }
    ;

INTEGER_TYPE
    :   'INTEGER'
    //|   'integer'
    ;

charType
    :   CHAR_TYPE LEFT_PARENTHESIS integerLiteral RIGHT_PARENTHESIS
    ;

CHAR_TYPE
    :   'CHAR'
    //|   'char'
    ;

varCharType
    :   VAR_CHAR_TYPE LEFT_PARENTHESIS integerLiteral RIGHT_PARENTHESIS
    ;

VAR_CHAR_TYPE
    :   'VARCHAR'
    //|   'varchar'
    ;

CREATE
    :   'CREATE'
    //|   'create'
    ;

TABLE
    :   'TABLE'
    //|   'table'
    ;

COMMA
    :   ','
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

id
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

DECIMAL_DIGIT
    :   '0' .. '9'
    ;

integerLiteral
    :   DECIMAL_DIGIT+
    ;