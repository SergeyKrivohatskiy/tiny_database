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
}
