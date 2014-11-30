grammar SQLGrammar;

options {
    language = Java;
}

@header {
    package ru.spbau.tinydb.grammar;

    import java.util.*;
    import ru.spbau.tinydb.queries.*;
    import ru.spbau.tinydb.expressions.bool.*;
    import ru.spbau.tinydb.expressions.comparison.*;
}

@members {

}

script returns [List<IQuery> result]
@init {
    $result = new ArrayList<>();
}
    :   ( query {
        $result.add($query.result);
    }
    )+ EOF
    ;

query returns [IQuery result]
@init {
    $result = null;
}
    :   ( ( createTable {
        $result = $createTable.result;
    }
    ) | ( createIndex {
        $result = $createIndex.result;
    }
    ) | ( selectFrom {
        $result = $selectFrom.result;
    }
    ) | ( insertInto {
        $result = $insertInto.result;
    }
    ) | ( deleteFrom {
        $result = $deleteFrom.result;
    }
    ) ) SEMICOLON
    ;

createTable returns [CreateTableQuery result]
@init {
    List<Attribute> attributes = new ArrayList<>();
}
    :   CREATE TABLE firstLevelId LEFT_PARENTHESIS attribute {
        attributes.add($attribute.result);
    }
    ( COMMA ( attribute {
        attributes.add($attribute.result);
    }
    ) )* RIGHT_PARENTHESIS {
        $result = new CreateTableQuery($firstLevelId.result, attributes);
    }
    ;

attribute returns [Attribute result]
    :   firstLevelId dataType {
        $result = new Attribute($firstLevelId.result, $dataType.result);
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
        $length = $integerLiteral.result;
    }
    ;

value returns [Object result]
    :   integerLiteral {
        $result = $integerLiteral.result;
    }
    |   doubleLiteral {
        $result = $doubleLiteral.result;
    }
    |   varCharLiteral {
        $result = $varCharLiteral.result;
    }
    ;

integerLiteral returns [Integer result]
    :   SIGN? DECIMAL_DIGIT+ {
        $result = new Integer($text);
    }
    ;

doubleLiteral returns [Double result]
    :   ( SIGN? DECIMAL_DIGIT+ DECIMAL_POINT DECIMAL_DIGIT*
    |   SIGN? DECIMAL_POINT? DECIMAL_DIGIT+ ) {
        $result = new Double($text);
    }
    ;

varCharLiteral returns [String result]
    :   QUOTES (~QUOTES)* QUOTES {
        $result = $text.replaceAll("\"", "");
    }
    ;

firstLevelId returns [String result]
    :   ( ( UNDERLINE idSuffix
    ) | ( (
            LOWER_CASE
        |   UPPER_CASE
        ) idSuffix?
    ) ) {
        $result = $text;
    }
    ;

idSuffix
    :   ( LOWER_CASE
    |   UPPER_CASE
    |   DECIMAL_DIGIT
    |   UNDERLINE
    )+
    ;

createIndex returns [CreateIndexQuery result]
@init {
    boolean isUnique = false;
    String indexName = null;
    String tableName = null;
    List<String> attributeNames = new ArrayList<>();
    boolean isAscending = true;
    boolean isUsingHash = true;
}
    :   CREATE ( UNIQUE {
        isUnique = true;
    }
    )? INDEX firstLevelId ON {
        indexName = $firstLevelId.result;
    }
    firstLevelId LEFT_PARENTHESIS {
        tableName = $firstLevelId.result;
    }
    firstLevelId {
        attributeNames.add($firstLevelId.result);
    }
    ( ASC | ( DESC {
        isAscending = false;
    }
    ) )? ( COMMA firstLevelId {
        attributeNames.add($firstLevelId.result);
    }
    )* RIGHT_PARENTHESIS USING ( ( BTREE {
        isUsingHash = false;
    }
    ) | HASH ) {
        $result = new CreateIndexQuery(tableName, indexName, attributeNames, isUnique, isAscending, isUsingHash);
    }
    ;

selectFrom returns [SelectFromQuery result]
@init {
    WhereCondition condition = null;
}
    :   SELECT filter FROM selectionTable ( whereCondition {
        condition = $whereCondition.result;
    }
    )? {
        $result = new SelectFromQuery($selectionTable.result, $filter.result, condition);
    }
    ;

insertInto returns [InsertIntoQuery result]
@init {
    String tableName = null;
    List<String> attributes = new ArrayList<>();
    List<Object> values = new ArrayList<>();
}
    :   INSERT INTO firstLevelId LEFT_PARENTHESIS {
        tableName = $firstLevelId.result;
    }firstLevelId {
        attributes.add($firstLevelId.result);
    }
    ( COMMA firstLevelId {
        attributes.add($firstLevelId.result);
    }
    )* RIGHT_PARENTHESIS VALUES LEFT_PARENTHESIS value {
        values.add($value.result);
    }
    ( COMMA value {
        values.add($value.result);
    }
    )* RIGHT_PARENTHESIS {
        $result = new InsertIntoQuery(tableName, attributes, values);
    }
    ;

deleteFrom returns [DeleteFromQuery result]
    :   DELETE FROM firstLevelId whereCondition {
        $result = new DeleteFromQuery($firstLevelId.result, $whereCondition.result);
    }
    ;

filter returns [List<String> result]
    :   ASTERISC {
        $result = null;
    }
    |   firstLevelId {
        $result = new ArrayList<>();
        $result.add($firstLevelId.result);
    }
    ( COMMA firstLevelId {
        $result.add($firstLevelId.result);
    }
    )*
    ;

whereCondition returns [WhereCondition result]
    :   WHERE booleanExpression {
        $result = new WhereCondition($booleanExpression.result);
    }
    ;

selectionTable returns [SelectionTable result]
@init {
    String tableName = null;
    List<JoinOnExpression> expressions = new ArrayList<>();
}
    :   firstLevelId {
        tableName = $firstLevelId.result;
    }
    ( joinOnExpression {
        expressions.add($joinOnExpression.result);
    }
    )* {
        $result = new SelectionTable(tableName, expressions);
    }
    ;

joinOnExpression returns [JoinOnExpression result]
@init {
    String tableName = null;
    SecondLevelId firstId = null;
}
    :   INNER JOIN firstLevelId ON secondLevelId EQUAL {
        tableName = $firstLevelId.result;
        firstId = $secondLevelId.result;
    }
    secondLevelId {
        $result = new JoinOnExpression(tableName, firstId, $secondLevelId.result);
    }
    ;

secondLevelId returns [SecondLevelId result]
@init {
    String tableName = null;
}
    :   firstLevelId ARROW {
        tableName = $firstLevelId.result;
    }
    firstLevelId {
        $result = new SecondLevelId(tableName, $firstLevelId.result);
    }
    ;

booleanExpression returns [BooleanExpression result]
@init {
    OrExpression first = null;
    OrExpression second = null;
}
    :   orExpression {
        first = $orExpression.result;
    }
    ( OR orExpression {
        second = $orExpression.result;
    }
    )? {
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
    }
    ( AND andExpression {
        second = $andExpression.result;
    }
    )? {
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
    |   comparisonExpression {
        $result = new BooleanFactor($comparisonExpression.result);
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

comparisonExpression returns [ComparisonExpression result]
    :   equalExpression {
        $result = $equalExpression.result;
    }
    |   notEqualExpression {
        $result = $notEqualExpression.result;
    }
    |   lessExpression {
        $result = $lessExpression.result;
    }
    |   lessOrEqualExpression {
        $result = $lessOrEqualExpression.result;
    }
    |   greaterExpression {
        $result = $greaterExpression.result;
    }
    |   greaterOrEqualExpression {
        $result = $greaterOrEqualExpression.result;
    }
    ;

equalExpression returns [EqualExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId EQUAL value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value EQUAL secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
        if (value instanceof Integer) {
            $result = new EqualExpression<Integer>(id, (Integer) value);
        } else if (value instanceof Double) {
            $result = new EqualExpression<Double>(id, (Double) value);
        } else {
            $result = new EqualExpression<String>(id, (String) value);
        }
    }
    ;

notEqualExpression returns [NotEqualExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId NOT_EQUAL value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value NOT_EQUAL secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
        if (value instanceof Integer) {
            $result = new NotEqualExpression<Integer>(id, (Integer) value);
        } else if (value instanceof Double) {
            $result = new NotEqualExpression<Double>(id, (Double) value);
        } else {
            $result = new NotEqualExpression<String>(id, (String) value);
        }
    }
    ;

lessExpression returns [LessExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId LESS value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value GREATER secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
        if (value instanceof Integer) {
            $result = new LessExpression<Integer>(id, (Integer) value);
        } else if (value instanceof Double) {
            $result = new LessExpression<Double>(id, (Double) value);
        } else {
            $result = new LessExpression<String>(id, (String) value);
        }
    }
    ;

lessOrEqualExpression returns [LessOrEqualExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId LESS_OR_EQUAL value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value GREATER_OR_EQUAL secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
        if (value instanceof Integer) {
            $result = new LessOrEqualExpression<Integer>(id, (Integer) value);
        } else if (value instanceof Double) {
            $result = new LessOrEqualExpression<Double>(id, (Double) value);
        } else {
            $result = new LessOrEqualExpression<String>(id, (String) value);
        }
    }
    ;

greaterExpression returns [GreaterExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId GREATER value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value LESS secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
        if (value instanceof Integer) {
            $result = new GreaterExpression<Integer>(id, (Integer) value);
        } else if (value instanceof Double) {
            $result = new GreaterExpression<Double>(id, (Double) value);
        } else {
            $result = new GreaterExpression<String>(id, (String) value);
        }
    }
    ;

greaterOrEqualExpression returns [GreaterOrEqualExpression result]
@init {
    SecondLevelId id = null;
    Object value = null;
}
    :   ( secondLevelId GREATER_OR_EQUAL value {
        id = $secondLevelId.result;
        value = $value.result;
    }
    |   value LESS_OR_EQUAL secondLevelId {
        id = $secondLevelId.result;
        value = $value.result;
    }
    ) {
          if (value instanceof Integer) {
              $result = new GreaterOrEqualExpression<Integer>(id, (Integer) value);
          } else if (value instanceof Double) {
              $result = new GreaterOrEqualExpression<Double>(id, (Double) value);
          } else {
              $result = new GreaterOrEqualExpression<String>(id, (String) value);
          }
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

UNIQUE
    :   'UNIQUE'
    ;

INDEX
    :   'INDEX'
    ;

USING
    :   'USING'
    ;

BTREE
    :   'BTREE'
    ;

HASH
    :   'HASH'
    ;

ASC
    :   'ASC'
    ;

DESC
    :   'DESC'
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

ARROW
    :   '->'
    ;

EQUAL
    :   '='
    ;

NOT_EQUAL
    :   '!='
    ;

LESS
    :   '<'
    ;

LESS_OR_EQUAL
    :   '<='
    ;

GREATER
    :   '>'
    ;

GREATER_OR_EQUAL
    :   '>='
    ;

QUOTES
    :   '"'
    ;