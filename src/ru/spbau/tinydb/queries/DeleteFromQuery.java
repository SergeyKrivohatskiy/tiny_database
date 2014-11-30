package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class DeleteFromQuery extends TableNameContainer implements IQuery {

    @NotNull
    private final WhereCondition filter;

    public DeleteFromQuery(@NotNull String tableName, @NotNull WhereCondition filter) {
        super(tableName);
        this.filter = filter;
    }

    @Override
    public void execute() {

    }

    @NotNull
    @Override
    public String toString() {
        return "DeleteFromQuery{" +
                "filter=" + filter +
                "} " + super.toString();
    }
}
