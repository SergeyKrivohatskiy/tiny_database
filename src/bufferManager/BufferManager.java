package bufferManager;

import java.io.Closeable;

/**
 * Created by Sergey on 09.10.2014.
 */
public interface BufferManager extends Closeable {
    /**
     * Returns page with specified pageIndex from specified set
     * returnPage should be called after page use
     * @param setId
     * @param pageIndex
     * @return page with specified pageId from specified set
     */
    Page getPage(int setId, int pageIndex);

    /**
     * This method should be called after page use
     * @param page
     */
    void returnPage(Page page);

    /**
     * Removes page with specified pageIndex from specified set
     * @param setId
     * @param pageIndex
     */
    void removePage(int setId, int pageIndex);

    /**
     * Creates page set
     * @return setId
     */
    int createPageSet();

    /**
     * Returns page with specified pageId from MetaInformation set that created automatically
     * returnPage should be called after page use
     * @param pageIndex
     * @return page with specified pageId from MetaInformation set
     */
    Page getMetaInformationPage(int pageIndex);
}
