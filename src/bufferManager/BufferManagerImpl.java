package bufferManager;

import com.google.common.cache.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 09.10.2014.
 */
public class BufferManagerImpl implements BufferManager {

    private static final long PAGE_CACHE_SIZE = 1;
    private static final long PAGE_SET_CACHE_SIZE = 3;
    private static final int DB_FILE_MAGIC = 0x54444248; // "TDBH". Magic that means Tiny DataBase Header
    private static final int VERSION = 1;
    private static final long PAGE_SETS_DESCR_POS = 8;

    private LoadingCache<Long, PageImpl> pageCache;
    private PageSetsDescriptor pageSetsDescriptor;
    private LoadingCache<Integer, PageSet> pageSets;
    private RandomAccessFile dbFile;

    public BufferManagerImpl(String dbFileName) throws IOException, ExecutionException {
        dbFile = new RandomAccessFile(dbFileName, "rw");
        boolean headerIsCorrect = checkHeader();
        if(!headerIsCorrect) {
            createNewDbFile();
        }
        createPageCache();
        createPageSetsCache();
        pageSetsDescriptor = new PageSetsDescriptorImpl(pageCache, PAGE_SETS_DESCR_POS, !headerIsCorrect);
    }

    @Override
    public Page getPage(int setId, int pageIndex) throws ExecutionException {
        return pageSets.get(setId).findPage(pageIndex);
    }

    @Override
    public void returnPage(Page page) {
        PageImpl pageImpl = (PageImpl) page;
        pageCache.put(pageImpl.getPos(), pageImpl);
    }

    @Override
    public void removePage(int setId, int pageIndex) throws ExecutionException {
            pageSets.get(setId).removePage(pageIndex);
    }

    @Override
    public int createPageSet() throws ExecutionException {
        return pageSetsDescriptor.createPageSet();
    }

    @Override
    public void close() throws IOException {
        pageCache.invalidateAll();
    }

    private void createPageSetsCache() {
        pageSets = CacheBuilder.newBuilder()
                .maximumSize(PAGE_SET_CACHE_SIZE)
                .build(new CacheLoader<Integer, PageSet>() {
                    @Override
                    public PageSet load(Integer setId) throws IOException, ExecutionException {
                        return pageSetsDescriptor.loadPageSet(setId);
                    }
                });
    }

    private void createPageCache() {
        pageCache = CacheBuilder.newBuilder()
                .maximumSize(PAGE_CACHE_SIZE)
                .removalListener(new RemovalListener<Long, PageImpl>() {
                    @Override
                    public void onRemoval(RemovalNotification<Long, PageImpl> notification) {
                        try {
                            notification.getValue().writeToFile(dbFile);
                        } catch (IOException e) {
                            // Cached page cant be written to file
                            System.exit(-1);
                        }
                    }
                })
                .build(new CacheLoader<Long, PageImpl>() {
                    @Override
                    public PageImpl load(Long pagePos) throws IOException {
                        return new PageImpl(pagePos, dbFile);
                    }
                });
    }

    private void createNewDbFile() throws IOException {
        dbFile.setLength(0);
        dbFile.seek(0);
        dbFile.writeInt(DB_FILE_MAGIC);
        dbFile.writeInt(VERSION);
    }

    private boolean checkHeader() {
        try {
            dbFile.seek(0);
            return dbFile.readInt() == DB_FILE_MAGIC && dbFile.readInt() == VERSION;
        } catch(IOException e) {
            return false;
        }
    }
}
