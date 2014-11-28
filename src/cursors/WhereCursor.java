package cursors;

import expression.Expression;

import java.util.Iterator;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Object[]> {

    private final Iterator<Object[]> baseCursor;
    private final Expression expression;
    private Object[] value;

    public WhereCursor(Iterator<Object[]> baseCursor, Expression expression) {
        this.expression = expression;
        this.baseCursor = baseCursor;
        value = getValue();
    }

    private Object[] getValue() {
        while(baseCursor.hasNext()) {
        	Object[] val = baseCursor.next();
            if (expression.check(val)) {
                return val;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return value != null;
    }

    @Override
    public Object[] next() {
    	Object[] old = value;
        value = getValue();
        return old;
    }

    @Override
    public void remove() {
        baseCursor.remove();
    }
}
