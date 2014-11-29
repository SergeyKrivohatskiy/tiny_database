package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class SelectionTable {

    @NotNull
    private final String tableName;
    @NotNull
    private final List<JoinOnExpression> expressions;

    public SelectionTable(@NotNull String tableName, @NotNull List<JoinOnExpression> expressions) {
        this.tableName = tableName;
        this.expressions = Collections.unmodifiableList(expressions);
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public List<JoinOnExpression> getExpressions() {
        return expressions;
    }

    @NotNull
    @Override
    public String toString() {
        return "SelectionTable{" +
                "tableName='" + tableName + '\'' +
                ", expressions=" + expressions +
                '}';
    }
}
