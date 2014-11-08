package table;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import queries.Attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class Table {
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
            default:
                // TODO implement others
                throw new RuntimeException("Unsupported attribute type");
        }
    }

    public int insertRecord(Map<String, byte[]> record) throws ExecutionException {
        byte[] row = new byte[recordSize];

        int pos = 0;
        for(Attribute attr: attributes) {
            byte[] attrValue = record.get(attr.getAttributeName());
            int len = getAttrSize(attr);
            System.arraycopy(attrValue, 0, row, pos, len);
            pos += len;
        }

        return baseTable.insert(row);
    }

    public Map<String, byte[]> getRecord(int recordId) throws ExecutionException {
        try (BufferView recordView = baseTable.get(recordId)) {
            int pos = 0;
            Map<String, byte[]> record = new HashMap<>();
            for(Attribute attr: attributes) {
                int len = getAttrSize(attr);
                byte[] attrValue = recordView.getBytes(pos, len);
                record.put(attr.getAttributeName(), attrValue);
                pos += len;
            }
            return record;
        }
    }
}
