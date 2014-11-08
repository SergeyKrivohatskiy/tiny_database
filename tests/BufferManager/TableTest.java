package BufferManager;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import queries.Attribute;
import table.AttributeValue;
import table.Table;
import table.TableBase;
import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class TableTest {

    private static final int COUNT = 1000000;
    private static final int RECORD_SIZE = 49;

    public static void main(String[] args) throws IOException, ExecutionException {
        testBaseTable();
        testTable();
    }

    private static void testTable() throws FileNotFoundException, ExecutionException, UnsupportedEncodingException {
        new File("db_test").delete();
        BufferManager bufferManager = new BufferManager("db_test");
        Collection<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Test int attr", Attribute.DataType.INTEGER));
        attributes.add(new Attribute("Test varchar attr", Attribute.DataType.VARCHAR));
        attributes.add(new Attribute("Test char attr", Attribute.DataType.CHAR));
        Table table = new Table(bufferManager, BufferManager.METAINF_FIRST_PAGE, attributes);
        Map<String, AttributeValue> record = new HashMap<>();
        record.put("Test int attr", new AttributeValue(123454));
        record.put("Test varchar attr", new AttributeValue("value"));
        byte[] c = new byte[1];
        c[0] = 50;
        record.put("Test char attr", new AttributeValue(Attribute.DataType.CHAR, c));
        for (int i = 0; i < COUNT; i ++) {
            table.insertRecord(record);
        }
        int i = 0;
        for(Map<String, AttributeValue> rec: table) {
            for(AttributeValue val: rec.values()) {
                switch (val.type) {
                    case INTEGER:
                        if(Utils.bytesToInt(val.value) != 123454) {
                            throw new RuntimeException();
                        }
                        break;
                    case CHAR:
                        if(!Arrays.equals(val.value, c)) {
                            throw new RuntimeException();
                        }
                        break;
                    case VARCHAR:
                        String attr = Utils.bytesToString(val.value);
                        if(!"value".equals(attr)) {
                            throw new RuntimeException();
                        }
                        break;
                }
            }
            i++;
        }
        if(i != COUNT) {
            throw new RuntimeException();
        }
    }

    private static void testBaseTable() throws FileNotFoundException, ExecutionException {
        new File("db_test_2").delete();
        BufferManager bufferManager = new BufferManager("db_test_2");
        TableBase table = new TableBase(bufferManager, BufferManager.METAINF_FIRST_PAGE, RECORD_SIZE);

        byte[] row = new byte[RECORD_SIZE];
        for (int i = 0; i < COUNT; i ++) {
            row[i % RECORD_SIZE] += 1;
            row[0] = 7;
            table.insert(row);
        }

        int i = 0;
        for(BufferView recordView: table) {
            i++;
            if(recordView.getByte(0) != 7) {
                throw new RuntimeException();
            }
            recordView.close();
        }
        if(i != COUNT) {
            throw new RuntimeException();
        }
    }

}
