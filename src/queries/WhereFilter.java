package queries;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public class WhereFilter {

    @NotNull
    private final String expression;

    public WhereFilter(@NotNull String expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "WhereFilter{" +
                "expression='" + expression + '\'' +
                '}';
    }
}
