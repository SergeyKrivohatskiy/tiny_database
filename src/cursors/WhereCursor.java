package cursors;

import table.AttributeValue;

import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class WhereCursor implements Iterator<Map<String, AttributeValue>> {

    private final Iterator<Map<String, AttributeValue>> baseCursor;
    private final Map<String, AttributeValue> attrValues;
    private Map<String, AttributeValue> value;

    public WhereCursor(Iterator<Map<String, AttributeValue>> baseCursor, Map<String, AttributeValue> attrValues) {
        this.attrValues = attrValues;
        this.baseCursor = baseCursor;
        value = getValue();
    }

    private Map<String, AttributeValue> getValue() {
        while(baseCursor.hasNext()) {
            Map<String, AttributeValue> val = baseCursor.next();
            if(check(val)) {
                return val;
            }
        }
        return null;
    }

    private boolean check(Map<String, AttributeValue> val) {
        for(Map.Entry<String, AttributeValue> attr: attrValues.entrySet()) {
            if(!attr.getValue().equals(val.get(attr.getKey()))) {
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
        baseCursor.remove();
    }
}
