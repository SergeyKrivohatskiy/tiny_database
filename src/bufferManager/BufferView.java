package bufferManager;

import utils.Utils;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Sergey on 27.10.2014.
 */
public class BufferView implements Closeable {
    private final int bufferPos;
    private final BufferManager bufferManager;
    private final byte[] buffer;
    private boolean closed = false;
    private final int offset;
    private final int length;

    public BufferView(byte[] buffer, int bufferPos, int length, BufferManager bufferManager) {
        this(buffer, bufferPos, 0, length, bufferManager);
    }

    private BufferView(byte[] buffer, int bufferPos, int offset, int length, BufferManager bufferManager) {
        this.bufferPos = bufferPos;
        this.bufferManager = bufferManager;
        this.buffer = buffer;
        this.offset = (int) (bufferPos * BufferManager.PAGE_SIZE) + offset;
        this.length = length;
        bufferManager.onBufferViewCreated(bufferPos);
    }

    public byte getByte(int idx) {
        checkClosed();
        checkIndex(idx, length);
        return buffer[offset + idx];
    }

    public void setByte(int idx, byte b) {
        checkClosed();
        checkIndex(idx, length);
        buffer[offset + idx] = b;
    }

    public byte[] getBytes(int idx, int length) {
        checkClosed();
        checkIndex(idx, this.length - length + 1);
        return Arrays.copyOfRange(buffer, offset + idx, offset + idx + length);
    }

    public void setBytes(int idx, byte[] row) {
        checkClosed();
        checkIndex(idx, length - row.length + 1);
        copyBytesFromIdx(idx, row);
    }

    public int getInt(int idx) {
        checkClosed();
        checkIndex(idx, length - 3);
        return ByteBuffer.wrap(buffer, offset + idx, 4).getInt();
    }

    public void setInt(int idx, int i) {
        checkClosed();
        checkIndex(idx, length - 3);
        byte[] iBytes = Utils.intToBytes(i);
        copyBytesFromIdx(idx, iBytes);
    }

    private void copyBytesFromIdx(int idx, byte[] iBytes) {
        for(int j = 0; j < iBytes.length; j ++) {
            buffer[offset + idx + j] = iBytes[j];
        }
    }

    private void checkIndex(int idx, int length) {
        if(idx < 0 || idx >= length) {
            throw new IllegalArgumentException();
        }
    }

    public BufferView getSubView(int offset, int length) {
        if(offset < 0 || length < 1 || length + offset > this.length) {
            throw new IllegalArgumentException("Illegal offset or length");
        }
        return new BufferView(buffer, bufferPos, offset, length, bufferManager);
    }

    @Override
    public void close() {
        checkClosed();
        if(!closed) {
            closed = true;
            bufferManager.onBufferViewRemoved(bufferPos);
            return;
        }
    }

    public void setChanged() {
        bufferManager.onChanged(bufferPos);
    }

    private void checkClosed() {
        if(closed) {
            throw new IllegalStateException("This BufferView was already closed");
        }
    }

}
