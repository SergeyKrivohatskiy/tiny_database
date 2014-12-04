package ru.spbau.tinydb.cursors;

import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;

import java.util.Iterator;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class NLJoinCursor implements Iterator<Object[]> {

    private final Iterator<Object[]> firstCursor;
    private Iterator<Object[]> secondCursor;
    private final Iterable<Object[]> iterable;
    private final JoinOnExpression eqAttrs;
    private Object[] value;
    private Object[] firstVal;

    public NLJoinCursor(Iterator<Object[]> firstCursor, Iterable<Object[]> iterable, JoinOnExpression eqAttrs) {
        this.firstCursor = firstCursor;
        this.iterable = iterable;
        this.eqAttrs = eqAttrs;
        value = getValue();
    }

    private Object[] getValue() {
        while(true) {
            if (firstVal == null && !firstCursor.hasNext()) {
                return null;
            }
            if (firstVal == null) {
                firstVal = firstCursor.next();
                secondCursor = iterable.iterator();
            }
            while (secondCursor.hasNext()) {
            	Object[] val = join(firstVal, secondCursor.next());

                // TODO implement
//                if (eqAttrs.execute(val)) {
//                    return val;
//                }
            }
            firstVal = null;
        }
    }

    private Object[] join(Object[] firstVal, Object[] secondVal) {
        Object[] result = new Object[firstVal.length + secondVal.length];
        System.arraycopy(firstVal, 0, result, 0, firstVal.length);
        System.arraycopy(secondVal, 0, result, firstVal.length, secondVal.length);
		return result ;
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
        throw new UnsupportedOperationException();
    }
}
