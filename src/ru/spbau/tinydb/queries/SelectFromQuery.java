package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.tinyDatabase.TinyDatabase;

import java.util.*;

/**
 * @author adkozlov
 */
public class SelectFromQuery implements IQuery<List<Map<SecondLevelId, Object>>> {

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
    public List<Map<SecondLevelId, Object>> call() throws DBException {
        // TODO rewrite with join
        Iterator<Map<SecondLevelId, Object>> selectAll = TinyDatabase.getInstance().selectAll(getTable().getTableName());
        Iterator<Map<SecondLevelId, Object>> cursor = new WhereCursor(selectAll, getFilter());

        List<Map<SecondLevelId, Object>> result = new ArrayList<>();
        while (cursor.hasNext()) {
            result.add(cursor.next());
        }

        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SelectFromQuery{" +
                "table=" + table +
                ", attributes=" + attributes +
                ", filter=" + filter +
                '}';
    }
}
