package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.common.DBException;

/**
 * @author adkozlov
 */
public class DeleteFromQuery extends TableNameContainer implements IQuery<Integer> {

    @NotNull
    private final WhereCondition filter;

    public DeleteFromQuery(@NotNull String tableName, @NotNull WhereCondition filter) {
        super(tableName);
        this.filter = filter;
    }

    @Override
    @NotNull
    public Integer execute() throws DBException {
        throw new DBException(new UnsupportedOperationException("unsupported delete operation"));
    }

    @NotNull
    @Override
    public String toString() {
        return "DeleteFromQuery{" +
                "filter=" + filter +
                "} " + super.toString();
    }
}
