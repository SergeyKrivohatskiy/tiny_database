package bufferManager;

import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class PageSetImpl implements PageSet {
    private static final int MAX_PAGE_POSITIONS = 100;
    private long[] pagePositions = new long[MAX_PAGE_POSITIONS];
    protected LoadingCache<Long, PageImpl> pageCache;
    private int pointers_loaded;
    private int where_begin;
    private int begin_index;
    private PageSetsDescriptor descriptor;
    private int setId;

    public PageSetImpl(LoadingCache<Long, PageImpl> pageCache_, long first_page_pos_, PageSetsDescriptorImpl pageSetsDescriptor, int setId_) {
        pageCache = pageCache_;
        pagePositions[0] = first_page_pos_;
        pointers_loaded = 1;
        where_begin = 0;
        begin_index = 0;
        descriptor = pageSetsDescriptor;
        setId = setId_;
    }

    @Override
    public void removePage(int index) throws ExecutionException {
        PageImpl page = findPage(index);
        where_begin = 0;
        pointers_loaded = 1;
        if(page.getPrev() != 0) {
            PageImpl prev = findPage(index - 1);
            prev.setNext(page.getNext());
            pageCache.put(prev.getPos(), prev);
            begin_index = index - 1;
            pagePositions[0] = prev.getPos();
        } else {
            descriptor.changeFirstPage(setId, page.getNext());
        }
        if(page.getNext() != 0) {
            PageImpl next = findPage(index - 1);
            next.setNext(page.getPrev());
            pageCache.put(next.getPos(), next);
            begin_index = index + 1;
            pagePositions[0] = next.getPos();
        }
        descriptor.returnFreePage(page);
    }

    @Override
    public PageImpl findPage(int page_index) throws ExecutionException {
        if (page_index < 0) {
            throw(new IndexOutOfBoundsException("Page index is " + page_index));
        }
        if (page_index >= begin_index && page_index < begin_index + pointers_loaded) {
            return pageCache.get(pagePositions[where_begin + page_index - begin_index]);
        }
        if (page_index < begin_index) {
            for (int i = begin_index; i > page_index; --i) {
                PageImpl currentPage = pageCache.get(pagePositions[where_begin]);
                if (pointers_loaded == 1 && currentPage.getNext() != 0) {
                    pagePositions[(where_begin + 1 + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS] = currentPage.getNext();
                    pointers_loaded += 1;
                }
                where_begin = (where_begin - 1 + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS;
                pagePositions[where_begin] = currentPage.getPrev();
                pointers_loaded = pointers_loaded == MAX_PAGE_POSITIONS ? MAX_PAGE_POSITIONS : pointers_loaded + 1;
                begin_index -= 1;
            }
            return pageCache.get(pagePositions[where_begin]);
        }
        for (int i = begin_index + pointers_loaded - 1; i < page_index; ++i) {
            PageImpl currentPage = pageCache.get(pagePositions[(where_begin + pointers_loaded - 1) % MAX_PAGE_POSITIONS]);
            if (pointers_loaded == 1 && currentPage.getPrev() != 0) {
                where_begin = (where_begin - 1 + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS;
                pagePositions[where_begin] = currentPage.getPrev();
                pointers_loaded += 1;
            }
            if (currentPage.getNext() == 0) {
                PageImpl newPage = descriptor.getFreePage(setId);
                currentPage.setNext(newPage.getPos());
                pageCache.put(currentPage.getPos(), currentPage);
                newPage.setNext(0);
                newPage.setPrev(currentPage.getPos());
                pageCache.put(newPage.getPos(), newPage);
            }
            if (pointers_loaded == MAX_PAGE_POSITIONS) {
                pagePositions[where_begin] = currentPage.getNext();
                where_begin = (where_begin + 1 + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS;
                begin_index += 1;
                continue;
            }
            pagePositions[(begin_index + pointers_loaded + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS] = currentPage.getNext();
            pointers_loaded += 1;
        }
        return pageCache.get(pagePositions[(where_begin + pointers_loaded - 1 + MAX_PAGE_POSITIONS) % MAX_PAGE_POSITIONS]);
    }
}
