package expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public Boolean execute() {
        Boolean firstValue = getFirst().execute();

        return getSecond() != null ? executeBinaryOperation(firstValue, getSecond().execute()) : firstValue;
    }

    protected abstract Boolean executeBinaryOperation(@NotNull Boolean first, @NotNull Boolean second);

    @NotNull
    @Override
    public String toString() {
        String result = getFirst().toString();

        return getSecond() != null ? "(" + result + binaryOperationToString() + getSecond().toString() + ")" : result;
    }

    protected abstract String binaryOperationToString();
}
