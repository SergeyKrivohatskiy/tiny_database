package BufferManager;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import queries.Attribute;
import table.Table;
import table.TableBase;
import utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class TableTester {

    private static final String TEST_DB_NAME = "db_test";
    private static final int COUNT = 1000;
    private static final int RECORD_SIZE = 49;

    public static void main(String[] args) throws IOException, ExecutionException {
        testBaseTable();
        testTable();
    }

    private static void testTable() throws FileNotFoundException, ExecutionException {
        new File(TEST_DB_NAME).delete();
        BufferManager bufferManager = new BufferManager(TEST_DB_NAME);
        Collection<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Test int attr", Attribute.DataType.INTEGER));
        Table table = new Table(bufferManager, BufferManager.METAINF_FIRST_PAGE, attributes);
        Map<String, byte[]> record = new HashMap<>();
        record.put("Test int attr", Utils.intToBytes(123454));
        int id = table.insertRecord(record);
        byte[] intBytes = table.getRecord(id).get("Test int attr");
        if(123454 != Utils.bytesToInt(intBytes)) {
            throw new RuntimeException();
        }
    }

    private static void testBaseTable() throws FileNotFoundException, ExecutionException {
        new File(TEST_DB_NAME).delete();
        BufferManager bufferManager = new BufferManager(TEST_DB_NAME);
        TableBase table = new TableBase(bufferManager, BufferManager.METAINF_FIRST_PAGE, RECORD_SIZE);

        List<Integer> recordIds = new ArrayList<>(COUNT);
        byte[] row = new byte[RECORD_SIZE];
        for (int i = 0; i < COUNT; i ++) {
            row[i % RECORD_SIZE] += 1;
            recordIds.add(table.insert(row));
        }
        Arrays.fill(row, (byte) 0);
        for (int i = 0; i < COUNT; i ++) {
            row[i % RECORD_SIZE] += 1;
            BufferView rowView = table.get(recordIds.get(i));
            for(int j = 0; j < row.length; j ++) {
                if(row[j] != rowView.getByte(j)) {
                    throw new RuntimeException();
                }
            }
            rowView.close();
        }
        Collections.shuffle(recordIds);
        for (int i = 0; i < COUNT; i ++) {
            BufferView rowView = table.get(recordIds.get(i));
            rowView.close();
        }
    }

}
