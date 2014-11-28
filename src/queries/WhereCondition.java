package queries;

import expressions.BooleanExpression;
import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class WhereCondition {

    @NotNull
    private final BooleanExpression condition;

    public WhereCondition(@NotNull BooleanExpression condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "WhereCondition{" +
                "condition=" + condition +
                '}';
    }
}
