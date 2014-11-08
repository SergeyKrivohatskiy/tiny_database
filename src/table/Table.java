package table;

import bufferManager.BufferView;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 17.10.2014.
 */
public interface Table extends Iterable<BufferView> {
    int insert(byte[] row) throws ExecutionException;
    bufferManager.BufferView get(int recordId) throws ExecutionException;
}
