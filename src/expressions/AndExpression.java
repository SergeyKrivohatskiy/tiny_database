package expressions;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class AndExpression extends AbstractBooleanExpression<BooleanFactor> {

    private final boolean negate;

    public AndExpression(@NotNull BooleanFactor first, boolean negate) {
        super(first);
        this.negate = negate;
    }

    @Override
    public Boolean execute() {
        boolean result = getFirst().execute();
        return !negate ? result : !result;
    }
}
