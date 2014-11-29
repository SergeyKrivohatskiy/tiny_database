package table;

import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.bufferManager.BufferView;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.Attribute.DataType;
import ru.spbau.tinydb.queries.Attribute.DoubleType;
import ru.spbau.tinydb.queries.Attribute.IntegerType;
import ru.spbau.tinydb.queries.Attribute.VarcharType;
import ru.spbau.tinydb.utils.Utils;

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
        if(attr.getDataType() instanceof IntegerType) {
        	return 4;
        }
        if(attr.getDataType() instanceof DoubleType) {
        	return 8;
        }
        if(attr.getDataType() instanceof VarcharType) {
        	VarcharType vcType = (VarcharType) attr.getDataType();
        	return vcType.getLength();
        }
        throw new RuntimeException("Unsupported attribute type");
    }

    public int insertRecord(Object[] record) throws ExecutionException, UnsupportedEncodingException {
        byte[] row = new byte[recordSize];

        int pos = 0;
        int i = 0;
        for(Attribute attr: attributes) {
            byte[] attrValue;
            attrValue = toByteArray(record[i], attr.getDataType());
            i += 1;
            System.arraycopy(attrValue, 0, row, pos, attrValue.length);
            pos += attrValue.length;
        }

        return baseTable.insert(row);
    }

    private byte[] toByteArray(Object object, DataType dataType) throws UnsupportedEncodingException {
        if(dataType instanceof IntegerType) {
        	return Utils.intToBytes((Integer)object);
        }
        if(dataType instanceof DoubleType) {
        	return Utils.doubleToBytes((Double)object);
        }
        if(dataType instanceof VarcharType) {
        	VarcharType vcType = (VarcharType) dataType;
        	return Utils.stringToBytes((String)object, vcType.getLength());
        }
        throw new RuntimeException("Unsupported attribute type");
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
            Object value = byteArrayToObject(attrValue, attr.getDataType());
            record[i] = value;
            i += 1;
            pos += len;
        }
        return record;
    }

    private Object byteArrayToObject(byte[] attrValue, DataType dataType) {
        if(dataType instanceof IntegerType) {
        	return Utils.bytesToInt(attrValue);
        }
        if(dataType instanceof DoubleType) {
        	return Utils.bytesToDouble(attrValue);
        }
        if(dataType instanceof VarcharType) {
        	return Utils.bytesToString(attrValue);
        }
        throw new RuntimeException("Unsupported attribute type");
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
