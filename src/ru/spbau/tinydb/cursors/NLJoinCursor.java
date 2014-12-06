package ru.spbau.tinydb.cursors;

import ru.spbau.tinydb.expressions.comparison.JoinOnExpression;
import ru.spbau.tinydb.queries.SecondLevelId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class NLJoinCursor implements Iterator<Map<SecondLevelId, Object>> {

    private final Iterator<Map<SecondLevelId, Object>> firstCursor;
    private Iterator<Map<SecondLevelId, Object>> secondCursor;
    private final Iterable<Map<SecondLevelId, Object>> iterable;
    private final JoinOnExpression eqAttrs;
    private Map<SecondLevelId, Object> value;
    private Map<SecondLevelId, Object> firstVal;

    public NLJoinCursor(Iterator<Map<SecondLevelId, Object>> firstCursor, Iterable<Map<SecondLevelId, Object>> iterable, JoinOnExpression eqAttrs) {
        this.firstCursor = firstCursor;
        this.iterable = iterable;
        this.eqAttrs = eqAttrs;
        value = getValue();
    }

    private Map<SecondLevelId, Object> getValue() {
        while(true) {
            if (firstVal == null && !firstCursor.hasNext()) {
                return null;
            }
            if (firstVal == null) {
                firstVal = firstCursor.next();
                secondCursor = iterable.iterator();
            }
            while (secondCursor.hasNext()) {
                Map<SecondLevelId, Object> val = join(firstVal, secondCursor.next());
                if (eqAttrs.execute(val)) {
                    return val;
                }
            }
            firstVal = null;
        }
    }

    private Map<SecondLevelId, Object> join(Map<SecondLevelId, Object> firstVal, Map<SecondLevelId, Object> secondVal) {
        Map<SecondLevelId, Object> result = new HashMap<>();
        result.putAll(firstVal);
        result.putAll(secondVal);
        return result;
    }
    
    @Override
    public boolean hasNext() {
        return value != null;
    }

    @Override
    public Map<SecondLevelId, Object> next() {
        Map<SecondLevelId, Object> old = value;
        value = getValue();
        return old;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
