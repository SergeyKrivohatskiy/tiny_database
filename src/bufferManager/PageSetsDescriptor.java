package bufferManager;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface PageSetsDescriptor {
    public PageSet loadPageSet(int id) throws ExecutionException;
    public int createPageSet() throws ExecutionException;
    public void returnFreePage(PageImpl freePage);
    public PageImpl getFreePage(int setId) throws ExecutionException;
    void changeFirstPage(int setId, long next) throws ExecutionException;
}
