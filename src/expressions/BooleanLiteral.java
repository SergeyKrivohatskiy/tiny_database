package expressions;

import org.jetbrains.annotations.NotNull;

/**
 * @author adkozlov
 */
public abstract class BooleanLiteral implements Expression<Boolean> {

    public static class TrueBooleanLiteral extends BooleanLiteral {

        private static final TrueBooleanLiteral INSTANCE = new TrueBooleanLiteral();

        private TrueBooleanLiteral() {
        }

        @NotNull
        public static TrueBooleanLiteral getInstance() {
            return INSTANCE;
        }

        @Override
        public Boolean execute() {
            return true;
        }

        @NotNull
        @Override
        public String toString() {
            return "TRUE";
        }
    }

    public static class FalseBooleanLiteral extends BooleanLiteral {

        private static final FalseBooleanLiteral INSTANCE = new FalseBooleanLiteral();

        private FalseBooleanLiteral() {
        }

        @NotNull
        public static FalseBooleanLiteral getInstance() {
            return INSTANCE;
        }

        @Override
        public Boolean execute() {
            return false;
        }

        @NotNull
        @Override
        public String toString() {
            return "FALSE";
        }
    }
}
