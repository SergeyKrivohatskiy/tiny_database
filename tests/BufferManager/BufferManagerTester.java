package BufferManager;

import bufferManager.BufferManager;
import bufferManager.BufferView;
import table.Table;
import table.TableImpl;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class BufferManagerTester {

    private static final String TEST_DB_NAME = "db_test";
    private static final int COUNT = 1000;
    private static final int RECORD_SIZE = 49;

    public static void main(String[] args) throws IOException, ExecutionException {
        new File(TEST_DB_NAME).delete();
        BufferManager bufferManager = new BufferManager(TEST_DB_NAME);
        Table table = new TableImpl(bufferManager, BufferManager.METAINF_FIRST_PAGE, RECORD_SIZE);

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
