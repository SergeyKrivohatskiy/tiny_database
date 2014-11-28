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
    protected Boolean executeBinaryOperation(@NotNull Boolean first, @NotNull Boolean second) {
        return first && second;
    }

    @Override
    protected String binaryOperationToString() {
        return " AND ";
    }
}
