package bufferManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by Sergey on 09.10.2014.
 */
public class PageImpl implements Page {
    static public final int PAGE_SIZE = 4 << 10;
    static public final int BUFFER_SIZE = PAGE_SIZE - 8 - 8;

    private long prev;
    private long next;
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private boolean headerChanged = false;
    private boolean bufferChanged = false;
    private long pageBegin;

    public PageImpl(long pageBegin_, RandomAccessFile input) throws IOException {
        pageBegin = pageBegin_;
        readPage(input);
    }

    public PageImpl(long pageBegin_, long prev_, long next_) {
        if(pageBegin_ == prev_ || pageBegin_ == next_) {
            assert false;
        }
        pageBegin = pageBegin_;
        prev = prev_;
        next = next_;
        headerChanged = true;
    }

    private void readPage(RandomAccessFile input) throws IOException {
        input.seek(pageBegin);
        prev = input.readLong();
        next = input.readLong();
        input.read(buffer.array());
        if(pageBegin == prev || pageBegin == next) {
            assert false;
        }
    }

    public void writeToFile(RandomAccessFile input) throws IOException {
        if(pageBegin == prev || pageBegin == next) {
            assert false;
        }
        if(headerChanged) {
            input.seek(pageBegin);
            input.writeLong(prev);
            input.writeLong(next);
            headerChanged = false;
        }
        if(bufferChanged) {
            input.seek(pageBegin + (PAGE_SIZE - BUFFER_SIZE));
            input.write(buffer.array());
            headerChanged = false;
        }
    }

    public long getPrev() {
        return prev;
    }

    public long getNext() {
        return next;
    }

    public void setPrev(long prev_) {
        if(prev != prev_) {
            headerChanged = true;
            prev = prev_;
        }
    }

    public void setNext(long next_) {
        if(next != next_) {
            headerChanged = true;
            next = next_;
        }
    }

    @Override
    public ByteBuffer getBuffer(boolean readOnly) {
        if(readOnly) {
            return buffer.asReadOnlyBuffer();
        }
        bufferChanged = true;
        return buffer;
    }

    public long getPos() {
        return pageBegin;
    }
}
