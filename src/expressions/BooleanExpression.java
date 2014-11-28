package expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author adkozlov
 */
public class BooleanExpression extends BinaryBooleanExpression<OrExpression> {

    public BooleanExpression(@NotNull OrExpression first, @Nullable OrExpression second) {
        super(first, second);
    }

    @Override
    public Boolean execute() {
        boolean firstValue = getFirst().execute();
        OrExpression second = getSecond();

        return second != null ? firstValue || second.execute() : firstValue;
    }
}
