package table;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import queries.Attribute;
import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class Table implements Iterable<Object[]> {
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

    public int insertRecord(Object[] record) throws ExecutionException, UnsupportedEncodingException {
        byte[] row = new byte[recordSize];

        int pos = 0;
        int i = 0;
        for(Attribute attr: attributes) {
            byte[] attrValue;
            switch (attr.getDataType()) {
			case INTEGER:
				attrValue = Utils.intToBytes((Integer)record[i]);
				break;
			case VARCHAR:
				attrValue = Utils.stringToBytes((String)record[i], 255);
				break;
			default:
				throw new RuntimeException();
			}
            i += 1;
            System.arraycopy(attrValue, 0, row, pos, attrValue.length);
            pos += attrValue.length;
        }

        return baseTable.insert(row);
    }

    public Object[] getRecord(int recordId) throws ExecutionException {
        try (BufferView recordView = baseTable.get(recordId)) {
            return getRecordFromView(recordView);
        }
    }

    private Object[] getRecordFromView(BufferView recordView) {
        int pos = 0;
        Object[] record = new Object[attributes.size()];
        int i = 0;
        for(Attribute attr: attributes) {
            int len = getAttrSize(attr);
            byte[] attrValue = recordView.getBytes(pos, len);
            Object value = null;
            switch (attr.getDataType()) {
			case INTEGER:
				value = new Integer(Utils.bytesToInt(attrValue));
				break;
			case VARCHAR:
				value = Utils.bytesToString(attrValue);
				break;
			default:
				throw new RuntimeException();
			}
            record[i] = value;
            i += 1;
            pos += len;
        }
        return record;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {
            private Iterator<BufferView> baseIterator = baseTable.iterator();

            @Override
            public boolean hasNext() {
                return baseIterator.hasNext();
            }

            @Override
            public Object[] next() {
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

    public Collection<Attribute> getSchema() {
        return attributes;
    }
}
