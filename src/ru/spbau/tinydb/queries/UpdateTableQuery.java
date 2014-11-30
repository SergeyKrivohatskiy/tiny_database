package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.expressions.AssignmentExpression;

import java.util.Collections;
import java.util.List;

/**
 * @author adkozlov
 */
public class UpdateTableQuery extends TableNameContainer implements IQuery {

    @NotNull
    private final List<AssignmentExpression> expressions;
    @NotNull
    private final WhereCondition filter;

    public UpdateTableQuery(@NotNull String tableName, @NotNull List<AssignmentExpression> expressions, @NotNull WhereCondition filter) {
        super(tableName);
        this.expressions = Collections.unmodifiableList(expressions);
        this.filter = filter;
    }

    @NotNull
    public List<AssignmentExpression> getExpressions() {
        return expressions;
    }

    @NotNull
    public WhereCondition getFilter() {
        return filter;
    }

    @Override
    public void execute() {

    }

    @NotNull
    @Override
    public String toString() {
        return "UpdateTableQuery{" +
                "expressions=" + expressions +
                ", filter=" + filter +
                "} " + super.toString();
    }
}
