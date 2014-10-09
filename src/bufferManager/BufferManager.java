package bufferManager;

import java.io.Closeable;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface BufferManager extends Closeable {
    /**
     * Returns page with specified pageIndex from specified set
     * returnPage should be called after page use
     * Page set gets additional pages from file if pageIndex >= page set size
     * @param setId
     * @param pageIndex
     * @return page with specified pageId from specified set
     */
    public Page getPage(int setId, int pageIndex);

    /**
     * Return current page set size
     * @param setId
     * @return page set size in pages
     */
    public int getPagesCount(int setId);

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
    public void removePage(int setId, int pageIndex);

    /**
     * Creates page set
     * @return setId
     */
    public int createPageSet();

    /**
     * Returns page with specified pageId from MetaInformation set that created automatically
     * returnPage should be called after page use
     * @param pageIndex
     * @return page with specified pageId from MetaInformation set
     */
    public Page getMetaInformationPage(int pageIndex);
}
