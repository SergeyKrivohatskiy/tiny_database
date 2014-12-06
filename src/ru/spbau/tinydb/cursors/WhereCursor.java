package ru.spbau.tinydb.cursors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.queries.WhereCondition;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Map<SecondLevelId, Object>> {

    @NotNull
    private final Iterator<Map<SecondLevelId, Object>> baseCursor;
    @Nullable
    private final WhereCondition condition;
    @Nullable
    private Map<SecondLevelId, Object> values;

    public WhereCursor(@NotNull Iterator<Map<SecondLevelId, Object>> baseCursor, @Nullable WhereCondition condition) {
        this.baseCursor = baseCursor;
        this.condition = condition;

        values = getValues();
    }

    @Nullable
    private Map<SecondLevelId, Object> getValues() {
        while (baseCursor.hasNext()) {
            Map<SecondLevelId, Object> values = baseCursor.next();

            if (condition == null || condition.check(values)) {
                return Collections.unmodifiableMap(values);
            }
        }

        return null;
    }

    @Override
    public boolean hasNext() {
        return values != null;
    }

    @Nullable
    @Override
    public Map<SecondLevelId, Object> next() {
        Map<SecondLevelId, Object> oldValues = values;
        values = getValues();

        return oldValues;
    }

    @Override
    public void remove() {
        throw new DBException("Remove is not implemented for where cursor");
    }
}
