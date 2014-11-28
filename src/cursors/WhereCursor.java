package cursors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import queries.SecondLevelId;
import queries.WhereCondition;

import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Map<SecondLevelId, Object>> {

    @NotNull
    private final Iterator<Map<SecondLevelId, Object>> baseCursor;
    @NotNull
    private final WhereCondition condition;
    @Nullable
    private Map<SecondLevelId, Object> values;

    public WhereCursor(@NotNull Iterator<Map<SecondLevelId, Object>> baseCursor, @NotNull WhereCondition condition) {
        this.baseCursor = baseCursor;
        this.condition = condition;

        values = getValues();
    }

    @Nullable
    private Map<SecondLevelId, Object> getValues() {
        while (baseCursor.hasNext()) {
            Map<SecondLevelId, Object> values = baseCursor.next();

            if (condition.check(values)) {
                return values;
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
        baseCursor.remove();
    }
}
