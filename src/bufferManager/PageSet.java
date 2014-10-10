package bufferManager;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface PageSet {
    public void removePage(int index) throws ExecutionException;
    public PageImpl findPage(int index) throws ExecutionException;
}
