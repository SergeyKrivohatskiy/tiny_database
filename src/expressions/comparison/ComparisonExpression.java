package expressions.comparison;

import common.DBException;
import expressions.Expression;
import expressions.PrintableOperation;
import org.jetbrains.annotations.NotNull;
import queries.SecondLevelId;

import java.util.Map;

/**
 * @author adkozlov
 */
public abstract class ComparisonExpression<V extends Comparable<V>> implements Expression<Boolean>, PrintableOperation {

    @NotNull
    private final SecondLevelId id;
    @NotNull
    private final V value;

    public ComparisonExpression(@NotNull SecondLevelId id, @NotNull V value) {
        this.id = id;
        this.value = value;
    }

    @NotNull
    public SecondLevelId getId() {
        return id;
    }

    @NotNull
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return id + " " + operationToString() + " " + value;
    }

    public int compareWithValueFromMap(Map<SecondLevelId, Object> values) {
        if (!values.containsKey(id)) {
            throw new DBException("no such attribute: " + id);
        }

        try {
            V mapValue = (V) values.get(id);
            return mapValue.compareTo(value);
        } catch (ClassCastException e) {
            throw new DBException("uncomparable types", e);
        }
    }
}
