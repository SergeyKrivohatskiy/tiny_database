package BufferManager;

import bufferManager.BufferManager;
import bufferManager.BufferManagerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class BufferManagerTester {

    private static final String TEST_DB_NAME = "db_test";
    private static final int META_PAGES_COUNT = 303;
    private static final int NEW_SETS_COUNT = 123;
    private static final int SETS_PAGES_COUNT = 321;

    public static void main(String[] args) throws IOException, ExecutionException {
        new File(TEST_DB_NAME).delete();
        BufferManager bufferManager = new BufferManagerImpl(TEST_DB_NAME);
        for (int i = 0; i < META_PAGES_COUNT; i++) {
            ByteBuffer buffer = bufferManager.getPage(BufferManager.META_INFORMATION_SET_ID, i).getBuffer(false);
            byte[] arr = buffer.array();

            for(int j = 0; j < arr.length; j++) {
                arr[j] = (byte) j;
            }
        }
        for (int i = 0; i < NEW_SETS_COUNT; i++) {
            int setId = bufferManager.createPageSet();
            for(int j = 0; j < SETS_PAGES_COUNT; j++) {
                ByteBuffer buffer = bufferManager.getPage(setId, j).getBuffer(false);
                byte[] arr = buffer.array();

                for(int k = 0; k < arr.length; k++) {
                    arr[j] = (byte) 1;
                }
            }
        }
        bufferManager.close();
        bufferManager = null;
        bufferManager = new BufferManagerImpl(TEST_DB_NAME);
        for (int i = 0; i < META_PAGES_COUNT; i++) {
            ByteBuffer buffer = bufferManager.getPage(BufferManager.META_INFORMATION_SET_ID, i).getBuffer(false);
            byte[] arr = buffer.array();

            for(int j = 0; j < arr.length; j++) {
                assert arr[j] == (byte) j;
            }
        }
        bufferManager.removePage(BufferManager.META_INFORMATION_SET_ID, 1);
        bufferManager.removePage(BufferManager.META_INFORMATION_SET_ID, 2);

        bufferManager.close();
    }
}
