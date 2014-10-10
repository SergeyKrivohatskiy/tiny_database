package bufferManager;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface BufferManager extends Closeable {
    public static int META_INFORMATION_SET_ID = 2;
    /**
     * Returns page with specified pageIndex from specified set
     * returnPage should be called after page use
     * Page set gets additional pages from file if pageIndex >= page set size
     * @param setId
     * @param pageIndex
     * @return page with specified pageId from specified set
     */
    public Page getPage(int setId, int pageIndex) throws ExecutionException;

    /**
     * This method should be called after page use
     * @param page
     */
    public void returnPage(Page page);

    /**
     * Removes page with specified pageIndex from specified set
     * @param setId
     * @param pageIndex
     */
    public void removePage(int setId, int pageIndex) throws ExecutionException;

    /**
     * Creates page set
     * @return setId
     */
    public int createPageSet() throws ExecutionException;
}
