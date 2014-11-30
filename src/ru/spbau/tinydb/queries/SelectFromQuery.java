package ru.spbau.tinydb.queries;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.tinyDatabase.TinyDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author adkozlov
 */
public class SelectFromQuery implements IQuery {

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
    public void execute() {
    	TinyDatabase db = TinyDatabase.getInstance();
    	// TODO rewrite with join
    	Iterator<Map<SecondLevelId, Object>> selectAll = db.selectAll(getTable().getTableName());
    	Iterator<Map<SecondLevelId, Object>> result = new WhereCursor(selectAll, getFilter());
    	
    	// TODO change
    	while(result.hasNext()) {
    		System.out.println(result.next());
    	}
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
