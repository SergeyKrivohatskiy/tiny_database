package cursors;

import table.AttributeValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class NLJoinCursor implements Iterator<Map<String, AttributeValue>> {

    private final Iterator<Map<String, AttributeValue>> firstCursor;
    private Iterator<Map<String, AttributeValue>> secondCursor;
    private final Iterable<Map<String, AttributeValue>> iterable;
    private final Set<String> eqAttrs;
    private Map<String, AttributeValue> value;
    private Map<String, AttributeValue> firstVal;

    public NLJoinCursor(Iterator<Map<String, AttributeValue>> firstCursor, Iterable<Map<String, AttributeValue>> iterable, Set<String> eqAttrs) {
        this.firstCursor = firstCursor;
        this.iterable = iterable;
        this.eqAttrs = eqAttrs;
        value = getValue();
    }

    private Map<String, AttributeValue> getValue() {
        while(true) {
            if (firstVal == null && !firstCursor.hasNext()) {
                return null;
            }
            if (firstVal == null) {
                firstVal = firstCursor.next();
                secondCursor = iterable.iterator();
            }
            while (secondCursor.hasNext()) {
                Map<String, AttributeValue> secondVal = secondCursor.next();
                if (check(firstVal, secondVal)) {
                    return join(firstVal, secondVal);
                }
            }
            firstVal = null;
        }
    }

    private Map<String, AttributeValue> join(Map<String, AttributeValue> firstVal, Map<String, AttributeValue> secondVal) {
        Map<String, AttributeValue> mapResult = new HashMap<>();
        for (String key : firstVal.keySet()) {
            mapResult.put(key, firstVal.get(key));
        }
        for (String key : secondVal.keySet()) {
            mapResult.put(key, secondVal.get(key));
        }
        return mapResult;
    }

    private boolean check(Map<String, AttributeValue> val1, Map<String, AttributeValue> val2) {
        for(String attrName: eqAttrs) {
            if(!val1.get(attrName).equals(val2.get(attrName))) {
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
    public Map<String, AttributeValue> next() {
        Map<String, AttributeValue> old = value;
        value = getValue();
        return old;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
