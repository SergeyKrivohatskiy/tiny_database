package expressions.bool;

import expressions.Expression;
import org.jetbrains.annotations.NotNull;
import queries.SecondLevelId;

import java.util.Map;

/**
 * @author adkozlov
 */
public class BooleanFactor extends AbstractBooleanExpression<Expression<Boolean>> {

    public BooleanFactor(@NotNull Expression<Boolean> first) {
        super(first);
    }

    @NotNull
    @Override
    public Boolean execute(@NotNull Map<SecondLevelId, Object> values) {
        return getFirst().execute(values);
    }
}
