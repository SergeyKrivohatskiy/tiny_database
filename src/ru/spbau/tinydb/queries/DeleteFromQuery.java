package ru.spbau.tinydb.queries;

import java.util.Iterator;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.cursors.WhereCursor;
import ru.spbau.tinydb.engine.IDataBase;

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
    public Integer execute(@NotNull IDataBase instance) throws DBException {
        Iterator<Map<SecondLevelId, Object>> selectAll = instance.selectAll(getTableName());
        int removed = 0;
       
        while(selectAll.hasNext()) {
            if(filter.check(selectAll.next())) {
                selectAll.remove();
                removed += 1;
            }
        }
        
        return removed;
    }

    @NotNull
    @Override
    public String toString() {
        return "DeleteFromQuery{" +
                "filter=" + filter +
                "} " + super.toString();
    }
}
