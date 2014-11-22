package cursors;

import java.util.Iterator;

import expresion.Expresion;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Object[]> {

    private final Iterator<Object[]> baseCursor;
    private final Expresion expresion;
    private Object[] value;

    public WhereCursor(Iterator<Object[]> baseCursor, Expresion expresion) {
        this.expresion = expresion;
        this.baseCursor = baseCursor;
        value = getValue();
    }

    private Object[] getValue() {
        while(baseCursor.hasNext()) {
        	Object[] val = baseCursor.next();
            if(expresion.check(val)) {
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
