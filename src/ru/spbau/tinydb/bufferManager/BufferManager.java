package ru.spbau.tinydb.bufferManager;

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
    private final static int BUFFER_SIZE = 1 << 10; // in PAGE_SIZE's
    public final static int METAINF_FIRST_PAGE = 1;

    private final RandomAccessFile dbFile;
    private final byte[] buffer = new byte[(int) (PAGE_SIZE * BUFFER_SIZE)];
    // 'i' page is used now if 'i' bit is set
    private final BitSet bufferIsUsed = new BitSet(BUFFER_SIZE);
    // 'i' page was recently used if 'i' bit is set
    private final BitSet bufferWasUsed = new BitSet(BUFFER_SIZE);
    // 'i' page should be written to disk if 'i' bit is set
    private final BitSet bufferChanged = new BitSet(BUFFER_SIZE);
    private final int[] usedPages = new int[BUFFER_SIZE];
    private final int[] pagesLoaded = new int[BUFFER_SIZE];
    private int currentPos = -1;
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
        
        // Clock algorithm
        while(true) {
	        currentPos = bufferIsUsed.nextClearBit(currentPos + 1);
	        if(currentPos == BUFFER_SIZE) {
	        	currentPos = bufferIsUsed.nextClearBit(0);
	        }
	        if(currentPos == BUFFER_SIZE) {
	            throw new RuntimeException("All buffer positions in use! Panic!");
	        }
	        if(bufferWasUsed.get(currentPos)) {
	        	bufferWasUsed.clear(currentPos);
	        } else {
	        	break;
	        }
        }
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
            bufferChanged.clear(bufferPos);
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
        bufferIsUsed.set(bufferPos);
        bufferWasUsed.set(bufferPos);
    }

    void onBufferViewRemoved(int bufferPos) {
        usedPages[bufferPos] -= 1;
        if(usedPages[bufferPos] == 0) {
            bufferIsUsed.clear(bufferPos);
        }
    }

    public void onChanged(int bufferPos) {
        bufferChanged.set(bufferPos);
    }

    public void flushBuffer() {
        for(int i = bufferChanged.nextSetBit(0); i != -1; i = bufferChanged.nextSetBit(i + 1)) {
            writePage(i);
        }
    }
}
