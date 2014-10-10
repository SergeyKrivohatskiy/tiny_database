package bufferManager;

import com.google.common.cache.LoadingCache;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class PageSetsDescriptorImpl implements PageSetsDescriptor {
    private static final int SET_REC_LEN = 8;
    private static final int SETS_PER_PAGE = PageImpl.BUFFER_SIZE / SET_REC_LEN;
    private int setsCount;
    private int pagesCount;
    private PageSetImpl freePageSet;
    private PageSetImpl metainfPageSet;
    private PageSetImpl descriptorsPageSet;
    private LoadingCache<Long, PageImpl> pageCache;

    public PageSetsDescriptorImpl(LoadingCache<Long, PageImpl> pageCache_, long first_page_pos_, boolean createNew) throws ExecutionException {
        pageCache = pageCache_;
        descriptorsPageSet = new PageSetImpl(pageCache, first_page_pos_, this, 0);
        PageImpl firstPage;
        ByteBuffer firstPageBuffer;
        if(createNew) {
            firstPage = new PageImpl(first_page_pos_, 0, 0);
            firstPageBuffer = firstPage.getBuffer(false);
            firstPageBuffer.putLong(0, PageImpl.PAGE_SIZE + first_page_pos_);
            firstPageBuffer.putLong(8, PageImpl.PAGE_SIZE * 2 + first_page_pos_);
            setsCount = 4;
            pagesCount = 3;
            firstPageBuffer.putInt(16, setsCount);
            firstPageBuffer.putInt(20, pagesCount);
            pageCache_.put(first_page_pos_, firstPage);
            metainfPageSet = new PageSetImpl(pageCache, PageImpl.PAGE_SIZE + first_page_pos_, this, 2);
            firstPage = new PageImpl(PageImpl.PAGE_SIZE + first_page_pos_, 0, 0);
            pageCache_.put(PageImpl.PAGE_SIZE + first_page_pos_, firstPage);
            freePageSet = new PageSetImpl(pageCache, PageImpl.PAGE_SIZE * 2 + first_page_pos_, this, 1);
            firstPage = new PageImpl(PageImpl.PAGE_SIZE * 2 + first_page_pos_, 0, 0);
            pageCache_.put(PageImpl.PAGE_SIZE * 2 + first_page_pos_, firstPage);
        } else {
            firstPage = descriptorsPageSet.findPage(0);
            firstPageBuffer = firstPage.getBuffer(true);
            metainfPageSet = new PageSetImpl(pageCache, firstPageBuffer.getLong(0), this, 2);
            freePageSet = new PageSetImpl(pageCache, firstPageBuffer.getLong(8), this, 1);
            setsCount = firstPageBuffer.getInt(16);
            pagesCount = firstPageBuffer.getInt(20);
        }
    }
    @Override
    public PageSetImpl loadPageSet(int id) throws ExecutionException {
        if(id == BufferManager.META_INFORMATION_SET_ID) {
            return metainfPageSet;
        }
        if(id >= setsCount) {
            return null;
        }
        int descrPageId = id / SETS_PER_PAGE;
        int setIndex = id % SETS_PER_PAGE;
        PageImpl descrPage = descriptorsPageSet.findPage(descrPageId);
        long firstPagePos = descrPage.getBuffer(true).getLong(setIndex * SET_REC_LEN);
        return new PageSetImpl(pageCache, firstPagePos, this, id);
    }

    @Override
    public int createPageSet() throws ExecutionException {
        int descrPageId = setsCount / SETS_PER_PAGE;
        int setIndex = setsCount % SETS_PER_PAGE;
        PageImpl descrPage = descriptorsPageSet.findPage(descrPageId);
        PageImpl freePage = getFreePage(setsCount);
        freePage.setPrev(0);
        freePage.setNext(0);
        pageCache.put(freePage.getPos(), freePage);
        descrPage.getBuffer(false).putLong(setIndex * SET_REC_LEN, freePage.getPos());
        pageCache.put(descrPage.getPos(), descrPage);
        PageImpl firstPage = descriptorsPageSet.findPage(0);
        setsCount += 1;
        firstPage.getBuffer(false).putInt(16, setsCount);
        pageCache.put(firstPage.getPos(), firstPage);
        return setsCount - 1;
    }

    @Override
    public void returnFreePage(PageImpl freePage) {

    }

    @Override
    public PageImpl getFreePage(int setId) throws ExecutionException {
        if(setId == 1) {
            PageImpl newPage = new PageImpl(PageImpl.PAGE_SIZE * pagesCount, 0, 0);
            pagesCount += 1;
            PageImpl firstPage = descriptorsPageSet.findPage(0);
            firstPage.getBuffer(false).putInt(20, pagesCount);
            pageCache.put(firstPage.getPos(), firstPage);
            return newPage;
        }
        PageImpl newPage = freePageSet.findPage(1);
        freePageSet.removePage(1);
        return newPage;
    }

    @Override
    public void changeFirstPage(int setId, long next) throws ExecutionException {
        int descrPageId = setId / SETS_PER_PAGE;
        int setIndex = setId % SETS_PER_PAGE;
        PageImpl descrPage = descriptorsPageSet.findPage(descrPageId);
        descrPage.getBuffer(false).putLong(setIndex * SET_REC_LEN, next);
    }
}
