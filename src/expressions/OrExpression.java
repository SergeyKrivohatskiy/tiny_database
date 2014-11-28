package expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author adkozlov
 */
public class OrExpression extends BinaryBooleanExpression<AndExpression> {

    public OrExpression(@NotNull AndExpression first, @Nullable AndExpression second) {
        super(first, second);
    }

    @Override
    public Boolean execute() {
        boolean firstValue = getFirst().execute();
        AndExpression second = getSecond();

        return second != null ? firstValue && second.execute() : firstValue;
    }
}
