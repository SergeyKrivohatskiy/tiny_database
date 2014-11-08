package bufferManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Created by Sergey on 27.10.2014.
 */
public class BufferManager {
    public final static long PAGE_SIZE = 1 << 12; // 4KB
    private final static int BUFFER_SIZE = 3; // in PAGE_SIZE's
    private final static int MAGIC = 0xFAFBFAFB; // rubbish
    public final static int METAINF_FIRST_PAGE = 1;

    private final RandomAccessFile dbFile;
    private final byte[] buffer = new byte[(int) (PAGE_SIZE * BUFFER_SIZE)];
    private final BitSet bufferAvailability = new BitSet(BUFFER_SIZE);
    private final BitSet bufferUsed = new BitSet(BUFFER_SIZE);
    private final BitSet bufferChanged = new BitSet(BUFFER_SIZE);
    private final int[] usedPages = new int[BUFFER_SIZE];
    private final int[] pagesLoaded = new int[BUFFER_SIZE];
    private int currentPos = 0;
    private int length;

    public BufferManager(String dbFileName) throws FileNotFoundException {
        this(new File(dbFileName));
    }

    public BufferManager(File dbFile) throws FileNotFoundException {
        this.dbFile = new RandomAccessFile(dbFile, "rw");
        init();
    }

    private void init() {
        Arrays.fill(pagesLoaded, -1);
        try {
            length = (int) (dbFile.length() / PAGE_SIZE);
            if(length == 0) {
                increaseLength();
                //writeHeader
                increaseLength(); // for MetaInf Page
            } else {
                // loadHeader
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException in BufferManager init method");
        }
    }

    private void increaseLength() {
        length += 1;
        try {
            dbFile.setLength(length * PAGE_SIZE);
        } catch (IOException e) {
            throw new RuntimeException("IOException when increasing file size");
        }
    }

    public int getFreePage() {
        increaseLength();
        return length - 1;
    }

    public BufferView getPage(int pageIndex) {
        // TODO May be change to Map
        for(int i = 0; i < BUFFER_SIZE; i++) {
            if(pageIndex == pagesLoaded[i]) {
                return new BufferView(buffer, i, (int) PAGE_SIZE, this);
            }
        }
        int firstAvailable = bufferAvailability.nextClearBit(currentPos);
        if(firstAvailable == BUFFER_SIZE) {
            firstAvailable = bufferAvailability.nextClearBit(0);
        }
        if(firstAvailable != BUFFER_SIZE) {
            currentPos = firstAvailable;
            loadPage(pageIndex, currentPos);
            bufferAvailability.set(currentPos);
            return new BufferView(buffer, currentPos, (int) PAGE_SIZE, this);
        }
        int notUsed = bufferUsed.nextClearBit(currentPos);
        if(notUsed == BUFFER_SIZE) {
            notUsed = bufferUsed.nextClearBit(0);
        }
        if(notUsed == BUFFER_SIZE) {
            throw new RuntimeException("All buffer positions in use! Panic!");
        }
        currentPos = notUsed;
        if(bufferChanged.get(currentPos)) {
            writePage(currentPos);
        }
        pagesLoaded[currentPos] = -1;
        loadPage(pageIndex, currentPos);
        return new BufferView(buffer, currentPos, (int) PAGE_SIZE, this);
    }

    private void writePage(int bufferPos) {
        try {
            int pageIndex = pagesLoaded[bufferPos];
            dbFile.seek(pageIndex * PAGE_SIZE);
            dbFile.write(buffer, (int) (bufferPos * PAGE_SIZE), (int) PAGE_SIZE);
            bufferChanged.clear(currentPos);
        } catch (IOException e) {
            throw new RuntimeException("IOException when writing page " + pagesLoaded[bufferPos] + " to " + bufferPos);
        }
    }

    private void loadPage(int pageIndex, int bufferPos) {
        try {
            dbFile.seek(pageIndex * PAGE_SIZE);
            dbFile.read(buffer, (int) (bufferPos * PAGE_SIZE), (int) PAGE_SIZE);
            pagesLoaded[bufferPos] = pageIndex;
        } catch (IOException e) {
            throw new RuntimeException("IOException when reading page " + pageIndex + " to " + bufferPos);
        }
    }

    void onBufferViewCreated(int bufferPos) {
        usedPages[bufferPos] += 1;
        bufferUsed.set(bufferPos);
    }

    void onBufferViewRemoved(int bufferPos) {
        usedPages[bufferPos] -= 1;
        if(usedPages[bufferPos] == 0) {
            bufferUsed.clear(bufferPos);
        }
    }

    public void onChanged(int bufferPos) {
        bufferChanged.set(bufferPos);
    }
}
