package BufferManager;

import bufferManager.BufferManager;
import metainformation.MetaInformationTable;
import queries.Attribute;
import table.AttributeValue;
import table.Table;
import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

        Table testTable = meta.createTable("testTable", Arrays.asList(new Attribute("testAttr", Attribute.DataType.VARCHAR)));

        Map<String, AttributeValue> record = new HashMap<String, AttributeValue>();
        record.put("testAttr", new AttributeValue("testValue"));
        testTable.insertRecord(record);

        testTable = meta.loadTable("testTable");

        for(Map<String, AttributeValue> rec: testTable) {
            if(!Utils.bytesToString(rec.get("testAttr").value).equals("testValue")) {
                throw new RuntimeException();
            }
        }

    }
}
