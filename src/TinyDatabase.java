import bufferManager.BufferManager;
import metainformation.MetaInformationTable;
import queries.Attribute;
import table.AttributeValue;
import table.Table;
import utils.Utils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class TinyDatabase {
    private final static String DB_FILENAME = "tiny.db";
    private static MetaInformationTable metaInf;
    private static BufferManager bufferManager;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ExecutionException {
        bufferManager = new BufferManager(DB_FILENAME);
        metaInf = new MetaInformationTable(bufferManager);

//        while(true) {
//            //get query
//
//            //execute query
//        }

        // WARNING: really bad sql syntax
        // Executing create table ...
        String tableName = "name";
        Collection<Attribute> schema = new ArrayList<>();
        schema.add(new Attribute("Test int attr", Attribute.DataType.INTEGER));
        schema.add(new Attribute("Test varchar attr", Attribute.DataType.VARCHAR));
        // check if table is already exists
        Table table = metaInf.loadTable(tableName);
        if(table != null) {
            System.out.println("table is already exists");
            System.out.println(table.getSchema());
        } else {
            metaInf.createTable(tableName, schema);
        }

        int COUNT = 1_000;
        // TODO: find bug: not all inserts(5 are missing) are visible after restarting, is it only for first page?
        // Executing insert ("Test int attr"=123, "Test varchar attr"="varchar value") into name x COUNT
        table = metaInf.loadTable(tableName);
        Map<String, AttributeValue> record = new HashMap<>();
        record.put("Test int attr", new AttributeValue(123));
        record.put("Test varchar attr", new AttributeValue("varchar value"));
        for(int i = 0; i < COUNT; i ++) {
            table.insertRecord(record);
        }

        // Executing insert ("Test int attr"=128, "Test varchar attr"="varchar value2") into name x COUNT
        table = metaInf.loadTable(tableName);
        record = new HashMap<>();
        record.put("Test int attr", new AttributeValue(128));
        record.put("Test varchar attr", new AttributeValue("varchar value2"));
        for(int i = 0; i < COUNT; i ++) {
            table.insertRecord(record);
        }

        // Executing select * from name where "Test int attr" = 123
        table = metaInf.loadTable(tableName);

        int count = 0, allCount = 0;
        for(Map<String, AttributeValue> rec: table) {
            AttributeValue intAttr = rec.get("Test int attr");
            assert intAttr.type == Attribute.DataType.INTEGER;
            if(Utils.bytesToInt(intAttr.value) == 123) {
                //System.out.println(rec);
                count ++;
            }
            allCount ++;
        }
        System.out.println(count);
        System.out.println(allCount);

        bufferManager.flushBuffer();
    }
}
