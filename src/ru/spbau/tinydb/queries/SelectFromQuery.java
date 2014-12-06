package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.cursors.NLJoinCursor;
import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.engine.IDataBase;
import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;

import java.util.*;

/**
 * @author adkozlov
 */
public class SelectFromQuery implements IQuery<Iterator<Map<SecondLevelId, Object>>> {

    @NotNull
    private final SelectionTable table;
    @NotNull
    private final List<String> attributes;
    @Nullable
    private final WhereCondition filter;

    public SelectFromQuery(@NotNull SelectionTable table, @Nullable List<String> attributes, @Nullable WhereCondition filter) {
        this.table = table;
        this.attributes = Collections.unmodifiableList(attributes != null ? attributes : new ArrayList<String>());
        this.filter = filter;
    }

    @NotNull
    public SelectionTable getTable() {
        return table;
    }

    @NotNull
    public List<String> getAttributes() {
        return attributes;
    }

    @Nullable
    public WhereCondition getFilter() {
        return filter;
    }

    @Override
    @NotNull
    public Iterator<Map<SecondLevelId, Object>> execute(@NotNull IDataBase instance) throws DBException {

        Iterator<Map<SecondLevelId, Object>> selectAll = instance.selectAll(getTable().getTableName());
        Iterator<Map<SecondLevelId, Object>> resultCursor = new WhereCursor(selectAll, getFilter());
        for(JoinOnExpression joinExpresion: table.getExpressions()) {
            resultCursor = new NLJoinCursor(resultCursor, instance.findTable(joinExpresion.getTableName()), joinExpresion);
        }
        return resultCursor;
    }

    @NotNull
    @Override
    public String toString() {
        return "SelectFromQuery{" +
                "table=" + table +
                ", attributes=" + attributes +
                ", filter=" + filter +
                "} " + super.toString();
    }
}
