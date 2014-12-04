package ru.spbau.tinydb.tinyDatabase;

import org.jetbrains.annotations.NotNull;
import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.common.DBException;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.queries.Attribute.DataType;
import ru.spbau.tinydb.queries.Attribute.DoubleType;
import ru.spbau.tinydb.queries.Attribute.IntegerType;
import ru.spbau.tinydb.queries.Attribute.VarcharType;
import ru.spbau.tinydb.queries.SecondLevelId;
import ru.spbau.tinydb.table.Table;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class TinyDatabase implements Closeable {

    private final static String DB_FILENAME = "tiny.db";

    @NotNull
    private static final TinyDatabase instance = new TinyDatabase();

    @NotNull
    private final MetaInformationTable metaInf;
    @NotNull
    private final BufferManager bufferManager;

    private TinyDatabase() {
        bufferManager = new BufferManager(DB_FILENAME);
        metaInf = new MetaInformationTable(bufferManager);
    }

    @NotNull
    public static TinyDatabase getInstance() {
        return instance;
    }

    @NotNull
    public Iterator<Map<SecondLevelId, Object>> selectAll(String name) {
        Table table = metaInf.loadTable(name);
        return table.iterator();
    }

    public void insertRecord(@NotNull String tableName, @NotNull List<String> attrs, @NotNull List<Object> record) throws DBException {
        Table table = metaInf.loadTable(tableName);

        Collection<Attribute> schema = table.getSchema();
        Object[] row = new Object[schema.size()];
        
        int i = 0;
        for(Attribute attr: schema) {
        	int idx = attrs.indexOf(attr.getAttributeName());
        	if(idx == -1) {
        		row[i] = getDefault(attr.getDataType());
        	} else {
        		row[i] = record.get(idx);
        	}
        	i += 1;
        }
        
        try {
            table.insertRecord(row);
        } catch (UnsupportedEncodingException | ExecutionException e) {
            throw new DBException(e);
        }
    }

    private Object getDefault(DataType dataType) {
        if (dataType instanceof IntegerType) {
            return 0;
        }
        if (dataType instanceof DoubleType) {
            return (double) 0;
        }
        if (dataType instanceof VarcharType) {
            return "";
        }

        throw new DBException("Unsupported attribute type");
	}

	public boolean createTable(@NotNull String tableName, @NotNull Collection<Attribute> schema) throws DBException {
        Table table = metaInf.loadTable(tableName);

        if (table == null) {
            try {
                metaInf.createTable(tableName, schema);
            } catch (UnsupportedEncodingException | ExecutionException e) {
                throw new DBException(e);
            }

            return true;
        } else {
            return false;
        }
    }

	@NotNull
	public Collection<Attribute> getTableSchema(String tableName) {
		return metaInf.loadTable(tableName).getSchema();
	}

    @Override
    public void close() throws IOException {
        flush();
    }

    public void flush() {
        bufferManager.flushBuffer();
    }
}
