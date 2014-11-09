package cursors;

import table.AttributeValue;

import java.util.Iterator;
import java.util.Map;

/**
 * tiny_database
 * Created by Sergey on 09.11.2014.
 */
public class RenameCursor implements Iterator<Map<String, AttributeValue>> {
    private final Iterator<Map<String, AttributeValue>> baseCursor;
    private final Map<String, String> renameRules;

    public RenameCursor(Iterator<Map<String, AttributeValue>> baseCursor, Map<String, String> renameRules) {
        this.baseCursor = baseCursor;
        this.renameRules = renameRules;
    }

    @Override
    public boolean hasNext() {
        return baseCursor.hasNext();
    }

    @Override
    public Map<String, AttributeValue> next() {
        Map<String, AttributeValue> value = baseCursor.next();
        for(Map.Entry<String, String> renameRule: renameRules.entrySet()) {
            AttributeValue attrVal = value.remove(renameRule.getKey());
            value.put(renameRule.getValue(), attrVal);
        }
        return value;
    }

    @Override
    public void remove() {

    }
}
