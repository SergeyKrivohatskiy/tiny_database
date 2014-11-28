package expressions;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class BooleanFactor extends AbstractBooleanExpression<Expression<Boolean>> {

    public BooleanFactor(@NotNull Expression<Boolean> first) {
        super(first);
    }

    @Override
    public Boolean execute() {
        return null;
    }
}
