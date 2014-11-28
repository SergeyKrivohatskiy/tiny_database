grammar SQLGrammar;

options {
language = Java;
}

@header {
package grammar;

import java.util.*;
import queries.*;
import expressions.*;
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
    ) | (   insertInto {
        $result = $insertInto.result;
    }
    ) | (   deleteFrom {
        $result = $deleteFrom.result;
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
        $result = Attribute.IntegerType.getInstance();
    }
    |   DOUBLE_TYPE {
        $result = Attribute.DoubleType.getInstance();
    }
    |   varCharType {
        $result = new Attribute.VarcharType($varCharType.length);
    }
    ;

varCharType returns [int length]
    :   VAR_CHAR_TYPE LEFT_PARENTHESIS integerLiteral RIGHT_PARENTHESIS {
        $length = $integerLiteral.intValue;
    }
    ;

value returns [Object attrValue]
    :   integerLiteral {
        $attrValue = $integerLiteral.intValue;
    }
    |   doubleLiteral {
        $attrValue = $doubleLiteral.doubleValue;
    }
    |   varCharLiteral {
        $attrValue = $varCharLiteral.stringValue;
    }
    ;

integerLiteral returns [Integer intValue]
    :   SIGN? DECIMAL_DIGIT+ {
        $intValue = new Integer($text);
    }
    ;

doubleLiteral returns [Double doubleValue]
    :   SIGN? DECIMAL_DIGIT+ DECIMAL_POINT DECIMAL_DIGIT*
    |   SIGN? DECIMAL_POINT? DECIMAL_DIGIT+ {
        $doubleValue = new Double($text);
    }
    ;

varCharLiteral returns [String stringValue]
    :   QUOTES (~QUOTES)* QUOTES {
        $stringValue = $text.replaceAll("\"", "");
    }
    ;

firstLevelId returns [String id]
    :   ( UNDERLINE idSuffix
    ) | ( (
            LOWER_CASE
        |   UPPER_CASE
        ) idSuffix?
    ) {
        $id = $text;
    }
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
    :   SELECT filter FROM table whereFilter {
        $result = new SelectFromQuery($table.text, $filter.result, $whereFilter.result);
    }
    ;

insertInto returns [InsertIntoQuery result]
@init {
    List<String> attributes = new ArrayList<>();
    List<Object> values = new ArrayList<>();
}
    :   INSERT INTO TABLE LEFT_PARENTHESIS firstLevelId {
        attributes.add($firstLevelId.text);
    }
    ( COMMA firstLevelId {
        attributes.add($firstLevelId.text);
    }
    )* RIGHT_PARENTHESIS VALUES LEFT_PARENTHESIS value {
        values.add($value.attrValue);
    }
    ( COMMA value {
        values.add($value.attrValue);
    }
    )* RIGHT_PARENTHESIS {
        $result = new InsertIntoQuery(attributes, values);
    }
    ;

deleteFrom returns [DeleteFromQuery result]
    :   DELETE FROM TABLE whereFilter {
        $result = new DeleteFromQuery($whereFilter.result);
    }
    ;

filter returns [List<String> result]
    :   ASTERISC {
        $result = null;
    }
    |   firstLevelId {
        $result = new ArrayList<>();
        $result.add($firstLevelId.text);
    } (COMMA firstLevelId {
        $result.add($firstLevelId.text);
    } )*
    ;

whereFilter returns [WhereFilter result]
    :   WHERE expression {
        $result = new WhereFilter($expression.text);
    }
    ;

table
    :   firstLevelId (join)*
    ;

expression // TODO
    :   EQUALS
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

booleanExpression returns [BooleanExpression result]
@init {
    OrExpression first = null;
    OrExpression second = null;
}
    :   orExpression {
        first = $orExpression.result;
    } (OR orExpression {
        second = $orExpression.result;
    } )? {
        $result = new BooleanExpression(first, second);
    }
    ;

orExpression returns [OrExpression result]
@init {
    AndExpression first = null;
    AndExpression second = null;
}
    :   andExpression {
        first = $andExpression.result;
    } (AND andExpression {
        second = $andExpression.result;
    } )? {
        $result = new OrExpression(first, second);
    }
    ;

andExpression returns [AndExpression result]
@init {
   boolean negate = false;
}
    :   ( NOT {
        negate = true;
    }
    )? booleanFactor {
        $result = new AndExpression($booleanFactor.result, negate);
    }
    ;

booleanFactor returns [BooleanFactor result]
    :   booleanLiteral {
        $result = new BooleanFactor($booleanLiteral.result);
    }
    |   LEFT_PARENTHESIS booleanExpression RIGHT_PARENTHESIS {
        $result = new BooleanFactor($booleanExpression.result);
    }
    ;

booleanLiteral returns [BooleanLiteral result]
    :   TRUE {
        $result = BooleanLiteral.TrueBooleanLiteral.getInstance();
    }
    |   FALSE {
        $result = BooleanLiteral.FalseBooleanLiteral.getInstance();
    }
    ;

OR
    :   'OR'
    ;

AND
    :   'AND'
    ;

NOT
    :   'NOT'
    ;

TRUE
    :   'TRUE'
    ;

FALSE
    :   'FALSE'
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

DELETE
    :   'DELETE'
    ;

INSERT
    :   'INSERT'
    ;

INTO
    :   'INTO'
    ;

VALUES
    :   'VALUES'
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

WHERE
    :   'WHERE'
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

QUOTES
    :   '"'
    ;