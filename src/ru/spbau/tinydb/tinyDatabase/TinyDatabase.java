package ru.spbau.tinydb.tinyDatabase;

import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import ru.spbau.tinydb.table.Table;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class TinyDatabase implements Closeable {
    private final static String DB_FILENAME = "tiny.db";
	private static TinyDatabase instance;
    private MetaInformationTable metaInf;
    private BufferManager bufferManager;

    public static TinyDatabase getInstance() throws Exception {
    	if(instance == null) {
    		instance = new TinyDatabase();
    	}
    	return instance;
    }
    
    private TinyDatabase() throws Exception {
        bufferManager = new BufferManager(DB_FILENAME);
        metaInf = new MetaInformationTable(bufferManager);
    }
    
    public Iterator<Object[]> selectAll() {
        Table table = metaInf.loadTable("name");
        return table.iterator();
    }

    public void insertRecord(String tableName, Object[] record) throws ClassCastException, ExecutionException, UnsupportedEncodingException {
        Table table;
        table = metaInf.loadTable(tableName);
        table.insertRecord(record);
    }
    
    public void flush() {
    	bufferManager.flushBuffer();
    }

    public boolean createTable(String tableName, Collection<Attribute> schema) throws UnsupportedEncodingException, ExecutionException {
        Table table = metaInf.loadTable(tableName);
        if(table != null) {
            return false;
        } else {
            metaInf.createTable(tableName, schema);
            return true;
        }
    }

	private Collection<Attribute> getTableSchema(String tableName) {
		return metaInf.loadTable(tableName).getSchema();
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}
