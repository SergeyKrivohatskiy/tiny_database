package BufferManager;

import ru.spbau.tinydb.bufferManager.BufferManager;
import ru.spbau.tinydb.metainformation.MetaInformationTable;
import ru.spbau.tinydb.queries.Attribute;
import table.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * tiny_database
 * Created by Sergey on 08.11.2014.
 */
public class MetaInfTest {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ExecutionException {
        new File("db_test").delete();
        BufferManager bufferManager = new BufferManager("db_test");
        MetaInformationTable meta = new MetaInformationTable(bufferManager);

        Table testTable = meta.createTable("testTable", Arrays.asList(new Attribute("testAttr", new Attribute.VarcharType(123))));

        Object[] record = new Object[1];
        record[0] = "testValue";
        testTable.insertRecord(record);

        testTable = meta.loadTable("testTable");

        for(Object[] rec: testTable) {
            if(!rec[0].equals("testValue")) {
                throw new RuntimeException();
            }
        }

    }
}
