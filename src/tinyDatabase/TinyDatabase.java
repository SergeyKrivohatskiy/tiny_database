package tinyDatabase;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import metainformation.MetaInformationTable;
import queries.Attribute;
import table.Table;
import utils.Utils;
import bufferManager.BufferManager;

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
    
    public void selectAll() {
        Table table = metaInf.loadTable("name");
        Utils.printSchema(table.getSchema());
        Utils.printAll(table.iterator());
    }

    public void insertRecord(String tableName, Object[] record) throws ExecutionException, UnsupportedEncodingException {
        Table table;
        table = metaInf.loadTable(tableName);
        table.insertRecord(record);
    }
    
    public void flush() {
    	bufferManager.flushBuffer();
    }

    public void createTable(String tableName, Collection<Attribute> schema) throws UnsupportedEncodingException, ExecutionException {
        Table table = metaInf.loadTable(tableName);
        if(table != null) {
            System.out.println("table is already exists and has this schema:");
            Utils.printSchema(table.getSchema());
        } else {
            metaInf.createTable(tableName, schema);
            System.out.println("table is created");
        }
    }

	@Override
	public void close() throws IOException {
		flush();
	}
}
