package cursors;

import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Object[]> {

    private final Iterator<Object[]> baseCursor;
    private final Map<Integer, Object> attrValues;
    private Object[] value;

    public WhereCursor(Iterator<Object[]> baseCursor, Map<Integer, Object> attrValues) {
        this.attrValues = attrValues;
        this.baseCursor = baseCursor;
        value = getValue();
    }

    private Object[] getValue() {
        while(baseCursor.hasNext()) {
        	Object[] val = baseCursor.next();
            if(check(val)) {
                return val;
            }
        }
        return null;
    }

    private boolean check(Object[] val) {
        for(Map.Entry<Integer, Object> attr: attrValues.entrySet()) {
            if(!attr.getValue().equals(val[attr.getKey()])) {
                return false;
            }
        }
        return true;
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
