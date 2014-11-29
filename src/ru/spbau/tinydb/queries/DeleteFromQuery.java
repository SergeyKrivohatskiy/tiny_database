package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class DeleteFromQuery implements IQuery {

    @NotNull
    private final WhereCondition filter;

    public DeleteFromQuery(@NotNull WhereCondition filter) {
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
                '}';
    }
}
