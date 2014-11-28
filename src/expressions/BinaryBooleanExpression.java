package expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import queries.SecondLevelId;

import java.util.Map;

/**
 * @author adkozlov
 */
public abstract class BinaryBooleanExpression<V extends Expression<Boolean>> extends AbstractBooleanExpression<V> {

    @Nullable
    private final V second;

    public BinaryBooleanExpression(@NotNull V first, @Nullable V second) {
        super(first);
        this.second = second;
    }

    @Nullable
    public V getSecond() {
        return second;
    }

    @NotNull
    @Override
    public Boolean execute(@NotNull Map<SecondLevelId, Object> values) {
        Boolean firstValue = getFirst().execute(values);

        return getSecond() != null ? executeBinaryOperation(firstValue, getSecond().execute(values)) : firstValue;
    }

    @NotNull
    protected abstract Boolean executeBinaryOperation(@NotNull Boolean first, @NotNull Boolean second);

    @NotNull
    @Override
    public String toString() {
        String result = getFirst().toString();

        return getSecond() != null ? "(" + result + binaryOperationToString() + getSecond().toString() + ")" : result;
    }

    @NotNull
    protected abstract String binaryOperationToString();
}
