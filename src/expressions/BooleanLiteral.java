package expressions;

/**
 * @author adkozlov
 */
public abstract class BooleanLiteral implements Expression<Boolean> {

    public static class TrueBooleanLiteral extends BooleanLiteral {

        private static final TrueBooleanLiteral INSTANCE = new TrueBooleanLiteral();

        private TrueBooleanLiteral() {
        }

        public static TrueBooleanLiteral getInstance() {
            return INSTANCE;
        }

        @Override
        public Boolean execute() {
            return true;
        }
    }

    public static class FalseBooleanLiteral extends BooleanLiteral {

        private static final FalseBooleanLiteral INSTANCE = new FalseBooleanLiteral();

        private FalseBooleanLiteral() {
        }

        public static FalseBooleanLiteral getInstance() {
            return INSTANCE;
        }

        @Override
        public Boolean execute() {
            return false;
        }
    }
}
