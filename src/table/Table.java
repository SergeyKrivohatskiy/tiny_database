package table;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import queries.Attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class Table implements Iterable<Map<String, AttributeValue>> {
    private final TableBase baseTable;
    private final int recordSize;
    private final Collection<Attribute> attributes;

    public Table(BufferManager bm, int firstPage, Collection<Attribute> attributes) {
        int recordSize = 0;
        for(Attribute attr: attributes) {
            recordSize += getAttrSize(attr);
        }
        this.recordSize = recordSize;
        this.attributes = attributes;
        baseTable = new TableBase(bm, firstPage, recordSize);
    }

    private int getAttrSize(Attribute attr) {
        switch (attr.getDataType()) {
            case INTEGER:
                return 4;
            case CHAR:
                return 1;
            case VARCHAR:
                //TODO change
                return 255;
            default:
                throw new RuntimeException("Unsupported attribute type");
        }
    }

    public int insertRecord(Map<String, AttributeValue>record) throws ExecutionException {
        byte[] row = new byte[recordSize];

        int pos = 0;
        for(Attribute attr: attributes) {
            byte[] attrValue = record.get(attr.getAttributeName()).value;
            System.arraycopy(attrValue, 0, row, pos, attrValue.length);
            pos += attrValue.length;
        }

        return baseTable.insert(row);
    }

    public Map<String, AttributeValue>getRecord(int recordId) throws ExecutionException {
        try (BufferView recordView = baseTable.get(recordId)) {
            return getRecordFromView(recordView);
        }
    }

    private Map<String, AttributeValue>getRecordFromView(BufferView recordView) {
        int pos = 0;
        Map<String, AttributeValue>record = new HashMap<>();
        for(Attribute attr: attributes) {
            int len = getAttrSize(attr);
            byte[] attrValue = recordView.getBytes(pos, len);
            record.put(attr.getAttributeName(), new AttributeValue(attr.getDataType(), attrValue));
            pos += len;
        }
        return record;
    }

    @Override
    public Iterator<Map<String, AttributeValue>> iterator() {
        return new Iterator<Map<String, AttributeValue>>() {
            private Iterator<BufferView> baseIterator = baseTable.iterator();

            @Override
            public boolean hasNext() {
                return baseIterator.hasNext();
            }

            @Override
            public Map<String, AttributeValue>next() {
                try(BufferView recordView = baseIterator.next()) {
                    return getRecordFromView(recordView);
                }
            }

            @Override
            public void remove() {
                baseIterator.remove();
            }
        };
    }
}
