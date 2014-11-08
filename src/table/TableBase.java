package table;

import bufferManager.BufferManager;
import bufferManager.BufferView;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sergey on 27.10.2014.
 */
public class TableBase {
    private final static int POINTERS_SIZE = 8;
    private final BufferManager bufferManager;
    private final int firstPage;
    private final int recordsPerPage;
    private final int recordSize;
    private final int bitSetSize;
    private int firstFullPage;
    private int firstNotFullPage;

    public TableBase(BufferManager bufferManager, int firstPage, int recordSize) {
        this.bufferManager = bufferManager;
        this.firstPage = firstPage;
        this.recordSize = recordSize;
        firstFullPage = 0;
        firstNotFullPage = 0;
        recordsPerPage = (int)(8 * (BufferManager.PAGE_SIZE - POINTERS_SIZE)) / (8 * recordSize + 1);
        bitSetSize = (recordsPerPage + 7) / 8;
        loadHeader();
    }


    private void writeHeader() {
        try (BufferView firstPageView = bufferManager.getPage(firstPage)) {
            firstPageView.setInt(0, firstNotFullPage);
            firstPageView.setInt(4, firstFullPage);
            firstPageView.setChanged();
        }

    }

    private void loadHeader() {
        try (BufferView firstPageView = bufferManager.getPage(firstPage)) {
            firstNotFullPage = firstPageView.getInt(0);
            firstFullPage = firstPageView.getInt(4);
        }
    }

    public int insert(byte[] row) throws ExecutionException {
        if(row.length != recordSize) {
            throw new IllegalArgumentException();
        }
        BufferView pageView;
        if(firstNotFullPage == 0) {
            firstNotFullPage = bufferManager.getFreePage();
            writeHeader();
            pageView = bufferManager.getPage(firstNotFullPage);
            //TODO fix
            //Arrays.fill(pageView.getByteBuffer().array(), (byte) 0);
            pageView.setChanged();
        } else {
            pageView = bufferManager.getPage(firstNotFullPage);
        }
        int pageIndex = firstNotFullPage;


        // writing row
        BufferView bitSetView = pageView.getSubView(POINTERS_SIZE, bitSetSize);
        BufferView recordsView = pageView.getSubView(POINTERS_SIZE + bitSetSize, recordsPerPage * recordSize);
        int recordPosition;
        // Hand made BitSet
        for(recordPosition = 0; (bitSetView.getByte(recordPosition / 8) & (1 << (recordPosition % 8))) != 0; recordPosition ++) {
            if(recordPosition >= recordsPerPage) {
                throw new RuntimeException();
            }
        }
        bitSetView.setByte(recordPosition / 8, (byte) (bitSetView.getByte(recordPosition / 8) | (1 << (recordPosition % 8))));
        recordsView.setBytes(recordPosition * recordSize, row);
        recordsView.close();

        boolean bitSetFull = true;
        for(int i = 0; i < recordsPerPage; i ++) {
            if((bitSetView.getByte(recordPosition / 8) & (1 << (recordPosition % 8))) == 0) {
                bitSetFull = false;
                break;
            }
        }
        bitSetView.close();
        if(bitSetFull) {
            // move to full
            int prevNotFull = pageView.getInt(0);
            int nextNotFull = pageView.getInt(4);
            if(prevNotFull != 0) {
                BufferView prevView = bufferManager.getPage(prevNotFull);
                prevView.setInt(4, nextNotFull);
                prevView.setChanged();
                prevView.close();
            } else {
                firstNotFullPage = 0;
                writeHeader();
            }
            if(nextNotFull != 0) {
                BufferView nextView = bufferManager.getPage(nextNotFull);
                nextView.setInt(0, prevNotFull);
                nextView.setChanged();
                nextView.close();
            }
            pageView.setInt(0, 0);
            pageView.setInt(4, firstFullPage);

            if(firstFullPage != 0) {
                BufferView fullView = bufferManager.getPage(firstFullPage);
                fullView.setInt(0, pageIndex);
                fullView.setChanged();
                fullView.close();
            }
            firstFullPage = pageIndex;
            writeHeader();
        }

        pageView.setChanged();
        pageView.close();
        return pageIndex * recordsPerPage + recordPosition;
    }

    public BufferView get(int recordId) throws ExecutionException {
        int pageIndex = recordId / recordsPerPage;
        int recordPosition = recordId % recordsPerPage;
        try (BufferView pageView = bufferManager.getPage(pageIndex)) {
            BufferView bitSetView = pageView.getSubView(POINTERS_SIZE, bitSetSize);
            boolean recordAvailable = (bitSetView.getByte(recordPosition / 8) & 1 << (recordPosition % 8)) != 0;
            bitSetView.close();
            if (recordAvailable) {
                return pageView.getSubView(POINTERS_SIZE + bitSetSize + recordPosition * recordSize, recordSize);
            }
            return null;
        }
    }

    public Iterator<BufferView> iterator() {
        return null;
    }
}
