package table;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 17.10.2014.
 */
public interface Table extends Iterable<byte[]> {
    int insert(byte[] row) throws ExecutionException;
    byte[] get(int pageId, int recordId) throws ExecutionException;
}
