package queries;

import expressions.bool.BooleanExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author adkozlov
 */
public class WhereCondition {

    @NotNull
    private final BooleanExpression condition;

    public WhereCondition(@NotNull BooleanExpression condition) {
        this.condition = condition;
    }

    public boolean check(Map<SecondLevelId, Object> values) {
        return condition.execute(values);
    }

    @Override
    public String toString() {
        return "WhereCondition{" +
                "condition=" + condition +
                '}';
    }
}
