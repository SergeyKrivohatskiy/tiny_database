import bufferManager.BufferManager;
import cursors.NLJoinCursor;
import cursors.RenameCursor;
import cursors.WhereCursor;
import metainformation.MetaInformationTable;
import queries.Attribute;
import table.AttributeValue;
import table.Table;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
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

        System.out.println("Executing create table ...");
        Collection<Attribute> schema = new ArrayList<>();
        schema.add(new Attribute("Test int attr", Attribute.DataType.INTEGER));
        schema.add(new Attribute("Test varchar attr", Attribute.DataType.VARCHAR));
        createTable("name", schema);

        System.out.println("Executing insert ('Test int attr'=123, 'Test varchar attr'='varchar value') into name");
        Table table;
        Map<String, AttributeValue> record = new HashMap<>();
        record.put("Test int attr", new AttributeValue(123));
        record.put("Test varchar attr", new AttributeValue("varchar value"));
        insertRecord("name", record);

        System.out.println("Executing insert ('Test int attr'=128, 'Test varchar attr'='varchar value2') into name");
        record = new HashMap<>();
        record.put("Test int attr", new AttributeValue(128));
        record.put("Test varchar attr", new AttributeValue("varchar value2"));
        insertRecord("name", record);

        System.out.println("Executing select * from name");
        table = metaInf.loadTable("name");
        printAll(table.iterator());

        System.out.println("Executing select * from name where 'Test int attr' = 123");
        table = metaInf.loadTable("name");
        Map<String, AttributeValue> attrVals = new HashMap<>();
        attrVals.put("Test int attr", new AttributeValue(123));
        printAll(new WhereCursor(table.iterator(), attrVals));

        System.out.println("Executing select * with rename 'Test int attr' to 'int attr'");
        table = metaInf.loadTable("name");
        Map<String, String> renameRules = new HashMap<>();
        renameRules.put("Test int attr", "int attr");
        printAll(new RenameCursor(table.iterator(), renameRules));

        System.out.println("Executing join by 'Test varchar attr' with rename " +
                "'Test int attr' to 'int attr' in first table");
        table = metaInf.loadTable("name");
        Set<String> eqAttr = new HashSet<>();
        eqAttr.add("Test varchar attr");
        printAll(new NLJoinCursor(new RenameCursor(table.iterator(), renameRules), table, eqAttr));

        bufferManager.flushBuffer();
    }

    private static void printAll(Iterator<Map<String, AttributeValue>> cursor) {
        while (cursor.hasNext()){
            System.out.println(cursor.next());
        }
    }

    private static void insertRecord(String tableName, Map<String, AttributeValue> record) throws ExecutionException {
        Table table;
        table = metaInf.loadTable(tableName);
        table.insertRecord(record);
    }

    private static void createTable(String tableName, Collection<Attribute> schema) throws UnsupportedEncodingException, ExecutionException {
        Table table = metaInf.loadTable(tableName);
        if(table != null) {
            System.out.println("table is already exists");
            System.out.println(table.getSchema());
        } else {
            metaInf.createTable(tableName, schema);
        }
    }
}
